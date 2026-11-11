package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtCompanyService;
import com.kdx.vms.service.IMtCustomerService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 组织与账号 REST API：公司管理 / 个人账号 / 客户支付账户
 */
@Controller
public class OrgApiController {

    private static final Logger logger = LoggerFactory.getLogger(OrgApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IMtCompanyService mtCompanyService;

    @Resource
    private IMtCustomerService mtCustomerService;

    @Resource
    private IMtVmService mtVmService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private com.kdx.vms.service.ISysTypeInfoService sysTypeInfoService;

    private TSysUser currentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return ((SSOToken) principal).getSysUser();
    }

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Internal error", ex);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 500);
        err.put("message", "服务器内部错误");
        return err;
    }

    // ==================== 公司管理 ====================

    /**
     * 公司列表（一级查二级/三级，二级查自己和子级，三级只看自己）
     */
    @RequestMapping(value = "/vms/api/company/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchName", required = false) String searchName) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String companyName = null;
        String customerName = null;
        String adminName = null;
        if (searchType != null && !StringUtil.isEmpty(searchName)) {
            if (searchType == 1) companyName = searchName;
            else if (searchType == 2) customerName = searchName;
            else if (searchType == 3) adminName = searchName;
        }

        Map<String, Object> model = mtCompanyService.list(companyName, customerName, adminName,
                pageSize, page, user, user.getObjectContext());
        Object nodeList = model.get("nodeList");

        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtCompany) {
                    TMtCompany c = (TMtCompany) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", c.getId());
                    row.put("companyName", c.getCompanyName());
                    row.put("companyLevel", c.getCompanyLevel());
                    row.put("maxVmCapacity", c.getMaxVmCapacity());
                    if (c.getCustomer() != null) {
                        row.put("customerId", c.getCustomer().getId());
                        row.put("customerName", c.getCustomer().getCustomerName());
                    }
                    if (c.getParentCompany() != null) {
                        row.put("parentCompanyId", c.getParentCompany().getId());
                        row.put("parentCompanyName", c.getParentCompany().getCompanyName());
                    }
                    row.put("ctime", c.getCtime() != null ? c.getCtime().toInstant().toString() : null);
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", model.get("totalSize"));
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 当前公司概览（层级、可用额度、是否可建公司/可升级）
     */
    @RequestMapping(value = "/vms/api/company/me", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyMe() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");

        TMtCompany company = user.getCompany();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", company.getId());
        row.put("companyName", company.getCompanyName());
        row.put("companyLevel", company.getCompanyLevel());
        row.put("maxVmCapacity", company.getMaxVmCapacity());
        boolean canCreate = company.getCompanyLevel() != null
                && company.getCompanyLevel() != TMtCompany.LEVEL_THREE;
        row.put("canCreateCompany", canCreate);
        boolean canUpgrade = company.getCompanyLevel() != null
                && company.getCompanyLevel() == TMtCompany.LEVEL_THREE
                && company.getParentCompany() != null
                && company.getParentCompany().getCompanyLevel() != null
                && company.getParentCompany().getCompanyLevel() == TMtCompany.LEVEL_ONE;
        row.put("canUpgrade", canUpgrade);
        return JSON.toJSONString(row);
    }

    /**
     * 未被公司关联的客户（建公司时选择）
     */
    @RequestMapping(value = "/vms/api/company/unusedCustomers", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String unusedCustomers() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        List<TMtCustomer> list = mtCustomerService.getUnusedTMtCustomer(user, user.getObjectContext());
        if (list != null) {
            for (TMtCustomer c : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", c.getId());
                row.put("customerName", c.getCustomerName());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 可升级为管理员的本公司用户（升级公司用）
     */
    @RequestMapping(value = "/vms/api/company/upgradeCandidates", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String upgradeCandidates() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        if (user.getCompany().getUser() != null) {
            for (TSysUser u : user.getCompany().getUser()) {
                if (u.getId() == user.getId()) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", u.getId());
                row.put("userName", u.getUserName());
                row.put("userAccount", u.getUserAccount());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 新建公司（含管理员账号）
     */
    @RequestMapping(value = "/vms/api/company/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyCreate(
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "adminName", required = false) String adminName,
            @RequestParam(value = "adminAccount", required = false) String adminAccount,
            @RequestParam(value = "userPhone", required = false) String userPhone,
            @RequestParam(value = "maxVmCapacity", required = false) Integer maxVmCapacity) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(companyName) || StringUtil.isEmpty(customerId)
                || StringUtil.isEmpty(adminName) || StringUtil.isEmpty(adminAccount)) {
            return errorJson("请填写公司名称、客户与管理员信息");
        }
        try {
            return JSON.toJSONString(mtCompanyService.addCompany(companyName, customerId, adminName,
                    adminAccount, userPhone, maxVmCapacity, user, user.getObjectContext()));
        } catch (Exception e) {
            logger.error("Create company failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新公司名称
     */
    @RequestMapping(value = "/vms/api/company/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyUpdate(
            @RequestParam(value = "ids", required = false) Integer ids,
            @RequestParam(value = "companyName", required = false) String companyName) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (ids == null || StringUtil.isEmpty(companyName)) return errorJson("参数不完整");

        try {
            Integer result = mtCompanyService.updateCompany(String.valueOf(ids), companyName, user, user.getObjectContext());
            if (result != null && result > 0) {
                Map<String, Object> ok = new LinkedHashMap<>();
                ok.put("code", 0);
                ok.put("type", "success");
                ok.put("message", "保存成功");
                return JSON.toJSONString(ok);
            }
            return errorJson("保存失败");
        } catch (Exception e) {
            logger.error("Update company failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 分配售货机额度
     */
    @RequestMapping(value = "/vms/api/company/addCapacity", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyAddCapacity(
            @RequestParam(value = "ids", required = false) Integer ids,
            @RequestParam(value = "newVmCapacitySum", required = false) Integer newVmCapacitySum) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (ids == null || newVmCapacitySum == null) return errorJson("参数不完整");
        if (user.getCompany().getCompanyLevel() != null
                && user.getCompany().getCompanyLevel() == TMtCompany.LEVEL_THREE) {
            return errorJson("三级公司无权分配额度");
        }
        if (ids.equals(user.getCompany().getId())) return errorJson("不能给自己分配额度");

        try {
            ObjectContext context = user.getObjectContext();
            TMtCompany target = mtCompanyService.getTMtCompanyById(ids, context);
            if (target == null) return errorJson("公司不存在");
            int oldSum = target.getMaxVmCapacity() == null ? 0 : target.getMaxVmCapacity();
            List<TMtVm> used = mtVmService.findMtVmListByCompanyId(context, ids);
            int usedCount = used == null ? 0 : used.size();
            if (newVmCapacitySum < usedCount) {
                return errorJson("新额度不能小于已使用数量（" + usedCount + "）");
            }
            int diff = newVmCapacitySum - oldSum;
            int selfCapacity = user.getCompany().getMaxVmCapacity() == null ? 0 : user.getCompany().getMaxVmCapacity();
            if (diff > selfCapacity) {
                return errorJson("超出自身可分配额度（" + selfCapacity + "）");
            }
            boolean ok = mtCompanyService.addCapacity(String.valueOf(ids), diff, user, context);
            if (ok) {
                Map<String, Object> okMap = new LinkedHashMap<>();
                okMap.put("code", 0);
                okMap.put("type", "success");
                okMap.put("message", "分配成功");
                return JSON.toJSONString(okMap);
            }
            return errorJson("分配失败");
        } catch (Exception e) {
            logger.error("Add capacity failed", e);
            return errorJson("分配失败：" + e.getMessage());
        }
    }

    /**
     * 升级公司（三级 → 二级，同时指定新管理员）
     */
    @RequestMapping(value = "/vms/api/company/upgrade", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyUpgrade(
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "newAdminUserId", required = false) String newAdminUserId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(companyName) || StringUtil.isEmpty(newAdminUserId)) {
            return errorJson("参数不完整");
        }
        try {
            return JSON.toJSONString(mtCompanyService.upgradeCompany(companyName, newAdminUserId,
                    user, user.getObjectContext()));
        } catch (Exception e) {
            logger.error("Upgrade company failed", e);
            return errorJson("升级失败：" + e.getMessage());
        }
    }

    // ==================== 个人账号 ====================

    /**
     * 当前账号信息
     */
    @RequestMapping(value = "/vms/api/account/me", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String accountMe() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", user.getId());
        row.put("userAccount", user.getUserAccount());
        row.put("userName", user.getUserName());
        row.put("userPhone", user.getUserPhone());
        row.put("userEmail", user.getUserEmail());
        if (user.getCompany() != null) {
            row.put("companyName", user.getCompany().getCompanyName());
        }
        return JSON.toJSONString(row);
    }

    /**
     * 修改姓名
     */
    @RequestMapping(value = "/vms/api/account/updateUserName", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String updateUserName(@RequestParam(value = "userName", required = false) String userName) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(userName)) return errorJson("请填写姓名");

        Boolean ok = sysUserService.updateUserName(user.getObjectContext(), user, userName);
        if (Boolean.TRUE.equals(ok)) {
            Map<String, Object> okMap = new LinkedHashMap<>();
            okMap.put("code", 0);
            okMap.put("type", "success");
            okMap.put("message", "修改成功");
            return JSON.toJSONString(okMap);
        }
        return errorJson("修改失败");
    }

    /**
     * 修改密码（旧逻辑需短信验证码，短信网关（UcPaas）已停用；
     * 改密前需先经原 /account/verifyUserPwdCode 流程或由管理员重置）
     */
    @RequestMapping(value = "/vms/api/account/updatePassword", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String updatePassword(@RequestParam(value = "pwd", required = false) String pwd) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(pwd)) return errorJson("请填写新密码");

        Boolean ok = sysUserService.updateUserPwd(user.getObjectContext(), user, pwd);
        if (Boolean.TRUE.equals(ok)) {
            Map<String, Object> okMap = new LinkedHashMap<>();
            okMap.put("code", 0);
            okMap.put("type", "success");
            okMap.put("message", "修改成功，下次登录生效");
            return JSON.toJSONString(okMap);
        }
        return errorJson("修改失败");
    }

    // ==================== 客户支付账户 ====================

    /**
     * 客户支付账户列表（一级公司看全部客户，其他层级看本公司客户）
     */
    @RequestMapping(value = "/vms/api/customerAccount/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String customerAccountList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        List<TMtCustomer> customers;
        if (user.getCompany().getCompanyLevel() != null
                && user.getCompany().getCompanyLevel() == TMtCompany.LEVEL_ONE) {
            Map<String, Object> model = mtCustomerService.list(null, null, null, null,
                    pageSize, page, user, user.getObjectContext());
            Object nodeList = model.get("nodeList");
            customers = new ArrayList<>();
            if (nodeList instanceof List) {
                for (Object obj : (List<?>) nodeList) {
                    if (obj instanceof TMtCustomer) customers.add((TMtCustomer) obj);
                }
            }
        } else {
            customers = new ArrayList<>();
            if (user.getCompany().getCustomer() != null) {
                customers.add(user.getCompany().getCustomer());
            }
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtCustomer c : customers) {
            rows.add(payAccountRow(c));
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", rows.size());
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 转账账号类型字典
     */
    @RequestMapping(value = "/vms/api/customerAccount/options", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String customerAccountOptions() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        com.kdx.vms.model.bean.TSysTypeInfo typeInfo =
                sysTypeInfoService.findSysTypeinfo(
                        com.kdx.vms.commons.util.SysTypeinfoEnum.GIRO_ACCOUNT_TYPE, user.getObjectContext());
        if (typeInfo != null && typeInfo.getDictionary() != null) {
            for (com.kdx.vms.model.bean.TSysDictionary d : typeInfo.getDictionary()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", d.getId());
                row.put("name", d.getDictionaryName());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 更新客户收款账户
     */
    @RequestMapping(value = "/vms/api/customerAccount/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String customerAccountUpdate(
            @RequestParam(value = "ids", required = false) Integer ids,
            @RequestParam(value = "collectionAccount", required = false) String collectionAccount,
            @RequestParam(value = "collectionAccountType", required = false) String collectionAccountType,
            @RequestParam(value = "giroAccount", required = false) String giroAccount,
            @RequestParam(value = "giroAccountName", required = false) String giroAccountName,
            @RequestParam(value = "giroAccountType", required = false) Integer giroAccountType) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (ids == null) return errorJson("参数不完整");

        try {
            ObjectContext context = user.getObjectContext();
            TMtCustomer customer = mtCustomerService.getTMtCustomerById(ids, context);
            if (customer == null) return errorJson("客户不存在");
            if (collectionAccount != null) customer.setCollectionAccount(collectionAccount);
            if (collectionAccountType != null) customer.setCollectionAccountType(collectionAccountType);
            if (giroAccount != null) customer.setGiroAccount(giroAccount);
            if (giroAccountName != null) customer.setGiroAccountName(giroAccountName);
            if (giroAccountType != null) customer.setGiroAccountType(giroAccountType);
            Integer result = mtCustomerService.updateCustomer(customer, user.getId(), context);
            if (result != null && result > 0) {
                Map<String, Object> ok = new LinkedHashMap<>();
                ok.put("code", 0);
                ok.put("type", "success");
                ok.put("message", "保存成功");
                return JSON.toJSONString(ok);
            }
            return errorJson("保存失败");
        } catch (Exception e) {
            try {
                user.getObjectContext().rollbackChanges();
            } catch (Exception ignore) {
            }
            logger.error("Update customer account failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    private static Map<String, Object> payAccountRow(TMtCustomer c) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", c.getId());
        row.put("customerName", c.getCustomerName());
        row.put("managerName", c.getManagerName());
        row.put("managerPhone", c.getManagerPhone());
        row.put("collectionAccount", c.getCollectionAccount());
        row.put("collectionAccountType", c.getCollectionAccountType());
        row.put("giroAccount", c.getGiroAccount());
        row.put("giroAccountName", c.getGiroAccountName());
        row.put("giroAccountType", c.getGiroAccountType());
        return row;
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
