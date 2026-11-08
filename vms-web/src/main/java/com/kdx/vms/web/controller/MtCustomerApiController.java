package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtCustomerService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 客户管理 REST API
 */
@Controller
@RequestMapping("/vms/api/customer")
public class MtCustomerApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtCustomerApiController.class);

    @Resource
    private IMtCustomerService mtCustomerService;

    @Resource
    private ISysTypeInfoService sysTypeInfoService;

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

    /**
     * 客户列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "customerType", required = false) String customerType,
            @RequestParam(value = "customerMode", required = false) String customerMode) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String customerName = null;
        String manageName = null;
        if (searchType != null && !StringUtil.isEmpty(searchName)) {
            if (searchType == 1) customerName = searchName;
            else if (searchType == 2) manageName = searchName;
        }

        Map<String, Object> model = mtCustomerService.list(customerName, manageName, customerMode,
                customerType, pageSize, page, user, user.getObjectContext());
        return pageResult(model, page, pageSize);
    }

    /**
     * 客户类型/模式下拉（字典）
     */
    @RequestMapping(value = "/options", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String options() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerType", dictOf(sysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_TYPE, context)));
        result.put("customerMode", dictOf(sysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_MODE, context)));
        return JSON.toJSONString(result);
    }

    /**
     * 新建客户
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "customerType", required = false) Integer customerType,
            @RequestParam(value = "customerMode", required = false) Integer customerMode,
            @RequestParam(value = "managerName", required = false) String managerName,
            @RequestParam(value = "managerPhone", required = false) String managerPhone,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestParam(value = "customerAddress", required = false) String customerAddress,
            @RequestParam(value = "taxCode", required = false) String taxCode) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(customerName)) return errorJson("请填写客户名称");

        try {
            ObjectContext context = user.getObjectContext();
            TMtCustomer customer = context.newObject(TMtCustomer.class);
            applyFields(customer, customerName, customerType, customerMode, managerName,
                    managerPhone, customerEmail, customerAddress, taxCode);
            Integer id = mtCustomerService.addCustomer(customer, user.getId(), context);
            if (id != null && id > 0) {
                Map<String, Object> ok = new LinkedHashMap<>();
                ok.put("code", 0);
                ok.put("type", "success");
                ok.put("message", "新建成功");
                ok.put("id", id);
                return JSON.toJSONString(ok);
            }
            return errorJson("新建失败");
        } catch (Exception e) {
            contextSafeRollback(user);
            logger.error("Create customer failed", e);
            return errorJson("新建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑客户
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "customerType", required = false) Integer customerType,
            @RequestParam(value = "customerMode", required = false) Integer customerMode,
            @RequestParam(value = "managerName", required = false) String managerName,
            @RequestParam(value = "managerPhone", required = false) String managerPhone,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestParam(value = "customerAddress", required = false) String customerAddress,
            @RequestParam(value = "taxCode", required = false) String taxCode) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (id == null || StringUtil.isEmpty(customerName)) return errorJson("参数不完整");

        try {
            ObjectContext context = user.getObjectContext();
            TMtCustomer customer = Cayenne.objectForPK(context, TMtCustomer.class, id);
            if (customer == null) return errorJson("客户不存在");
            applyFields(customer, customerName, customerType, customerMode, managerName,
                    managerPhone, customerEmail, customerAddress, taxCode);
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
            contextSafeRollback(user);
            logger.error("Update customer failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除客户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的客户");

        try {
            boolean ok = mtCustomerService.delCustomer(idsCsv.split(","), user.getObjectContext());
            if (ok) {
                Map<String, Object> okMap = new LinkedHashMap<>();
                okMap.put("code", 0);
                okMap.put("type", "success");
                okMap.put("message", "删除成功");
                return JSON.toJSONString(okMap);
            }
            return errorJson("删除失败");
        } catch (Exception e) {
            logger.error("Delete customer failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static void applyFields(TMtCustomer c, String customerName, Integer customerType, Integer customerMode,
                                    String managerName, String managerPhone, String customerEmail,
                                    String customerAddress, String taxCode) {
        c.setCustomerName(customerName);
        if (customerType != null) c.setCustomerType(customerType);
        if (customerMode != null) c.setCustomerMode(customerMode);
        c.setManagerName(managerName);
        c.setManagerPhone(managerPhone);
        c.setCustomerEmail(customerEmail);
        c.setCustomerAddress(customerAddress);
        c.setTaxCode(taxCode);
    }

    private static void contextSafeRollback(TSysUser user) {
        try {
            user.getObjectContext().rollbackChanges();
        } catch (Exception ignore) {
        }
    }

    private static List<Map<String, Object>> dictOf(TSysTypeInfo typeInfo) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (typeInfo != null && typeInfo.getDictionary() != null) {
            for (TSysDictionary d : typeInfo.getDictionary()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", d.getId());
                row.put("name", d.getDictionaryName());
                rows.add(row);
            }
        }
        return rows;
    }

    private static String pageResult(Map<String, Object> model, int page, int pageSize) {
        Object nodeList = model.get("nodeList");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtCustomer) {
                    TMtCustomer c = (TMtCustomer) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", c.getId());
                    row.put("customerName", c.getCustomerName());
                    row.put("customerType", c.getCustomerType());
                    row.put("customerMode", c.getCustomerMode());
                    row.put("managerName", c.getManagerName());
                    row.put("managerPhone", c.getManagerPhone());
                    row.put("customerEmail", c.getCustomerEmail());
                    row.put("customerAddress", c.getCustomerAddress());
                    row.put("taxCode", c.getTaxCode());
                    row.put("companyCount", c.getCompany() != null ? c.getCompany().size() : 0);
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

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
