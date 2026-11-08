package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtPartnerService;
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
 * 合伙人管理 REST API
 */
@Controller
@RequestMapping("/vms/api/partner")
public class MtPartnerApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtPartnerApiController.class);

    @Resource
    private IMtPartnerService mtPartnerService;

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
     * 合伙人列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "partnerType", required = false) String partnerType) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String partnerName = null;
        String manageName = null;
        if (searchType != null && !StringUtil.isEmpty(searchName)) {
            if (searchType == 1) partnerName = searchName;
            else if (searchType == 2) manageName = searchName;
        }

        Map<String, Object> model = mtPartnerService.list(partnerName, manageName, partnerType,
                pageSize, page, user, user.getObjectContext());

        Object nodeList = model.get("nodeList");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtPartner) {
                    TMtPartner p = (TMtPartner) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", p.getId());
                    row.put("partnerName", p.getPartnerName());
                    row.put("partnerType", p.getPartnerType());
                    row.put("managerName", p.getManagerName());
                    row.put("managerPhone", p.getManagerPhone());
                    row.put("partnerEmail", p.getPartnerEmail());
                    row.put("partnerAddress", p.getPartnerAddress());
                    row.put("taxCode", p.getTaxCode());
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
     * 合伙人类型下拉（字典）
     */
    @RequestMapping(value = "/options", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String options() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        TSysTypeInfo typeInfo = sysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.PARTNER_TYPE, user.getObjectContext());
        List<Map<String, Object>> rows = new ArrayList<>();
        if (typeInfo != null && typeInfo.getDictionary() != null) {
            for (TSysDictionary d : typeInfo.getDictionary()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", d.getId());
                row.put("name", d.getDictionaryName());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 新建合伙人
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "partnerName", required = false) String partnerName,
            @RequestParam(value = "partnerType", required = false) Integer partnerType,
            @RequestParam(value = "managerName", required = false) String managerName,
            @RequestParam(value = "managerPhone", required = false) String managerPhone,
            @RequestParam(value = "partnerEmail", required = false) String partnerEmail,
            @RequestParam(value = "partnerAddress", required = false) String partnerAddress,
            @RequestParam(value = "taxCode", required = false) String taxCode) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(partnerName)) return errorJson("请填写合伙人名称");

        try {
            ObjectContext context = user.getObjectContext();
            TMtPartner partner = context.newObject(TMtPartner.class);
            applyFields(partner, partnerName, partnerType, managerName, managerPhone,
                    partnerEmail, partnerAddress, taxCode);
            Integer id = mtPartnerService.addPartner(partner, user, context);
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
            safeRollback(user);
            logger.error("Create partner failed", e);
            return errorJson("新建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑合伙人
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "partnerName", required = false) String partnerName,
            @RequestParam(value = "partnerType", required = false) Integer partnerType,
            @RequestParam(value = "managerName", required = false) String managerName,
            @RequestParam(value = "managerPhone", required = false) String managerPhone,
            @RequestParam(value = "partnerEmail", required = false) String partnerEmail,
            @RequestParam(value = "partnerAddress", required = false) String partnerAddress,
            @RequestParam(value = "taxCode", required = false) String taxCode) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (id == null || StringUtil.isEmpty(partnerName)) return errorJson("参数不完整");

        try {
            ObjectContext context = user.getObjectContext();
            TMtPartner partner = Cayenne.objectForPK(context, TMtPartner.class, id);
            if (partner == null) return errorJson("合伙人不存在");
            applyFields(partner, partnerName, partnerType, managerName, managerPhone,
                    partnerEmail, partnerAddress, taxCode);
            Integer result = mtPartnerService.updatePartner(partner, user.getId(), context);
            if (result != null && result > 0) {
                Map<String, Object> ok = new LinkedHashMap<>();
                ok.put("code", 0);
                ok.put("type", "success");
                ok.put("message", "保存成功");
                return JSON.toJSONString(ok);
            }
            return errorJson("保存失败");
        } catch (Exception e) {
            safeRollback(user);
            logger.error("Update partner failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除合伙人
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的合伙人");

        try {
            boolean ok = mtPartnerService.delPartner(idsCsv.split(","), user.getObjectContext());
            if (ok) {
                Map<String, Object> okMap = new LinkedHashMap<>();
                okMap.put("code", 0);
                okMap.put("type", "success");
                okMap.put("message", "删除成功");
                return JSON.toJSONString(okMap);
            }
            return errorJson("删除失败");
        } catch (Exception e) {
            logger.error("Delete partner failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static void applyFields(TMtPartner p, String partnerName, Integer partnerType, String managerName,
                                    String managerPhone, String partnerEmail, String partnerAddress, String taxCode) {
        p.setPartnerName(partnerName);
        if (partnerType != null) p.setPartnerType(partnerType);
        p.setManagerName(managerName);
        p.setManagerPhone(managerPhone);
        p.setPartnerEmail(partnerEmail);
        p.setPartnerAddress(partnerAddress);
        p.setTaxCode(taxCode);
    }

    private static void safeRollback(TSysUser user) {
        try {
            user.getObjectContext().rollbackChanges();
        } catch (Exception ignore) {
        }
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
