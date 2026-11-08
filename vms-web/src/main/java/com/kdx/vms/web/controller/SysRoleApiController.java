package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysRoleService;
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
 * 角色管理 REST API
 */
@Controller
@RequestMapping("/vms/api/sysRole")
public class SysRoleApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysRoleApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private ISysRoleService sysRoleService;

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
     * 角色列表（当前公司 + 公共角色）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "roleName", required = false) String roleName) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        List full = sysRoleService.getSysRoleCompany(user.getCompany(), roleName, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = full.get(i);
            if (obj instanceof TSysRole) {
                TSysRole r = (TSysRole) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", r.getId());
                row.put("roleName", r.getRoleName());
                row.put("roleDesc", r.getRoleDesc());
                row.put("roleType", r.getRoleType());
                row.put("roleTypeName", roleTypeName(r.getRoleType()));
                row.put("userCount", r.getUserRole() != null ? r.getUserRole().size() : 0);
                row.put("ctime", r.getCtime() != null ? r.getCtime().toInstant().toString() : null);
                rows.add(row);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 角色详情（含已绑定权限）
     */
    @RequestMapping(value = "/{roleId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("roleId") Integer roleId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TSysRole role = Cayenne.objectForPK(context, TSysRole.class, roleId);
        if (role == null) return notFound("角色不存在");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", role.getId());
        result.put("roleName", role.getRoleName());
        result.put("roleDesc", role.getRoleDesc());
        result.put("roleType", role.getRoleType());
        result.put("roleTypeName", roleTypeName(role.getRoleType()));
        result.put("elementIds", parseIdList(sysRoleService.getElementIdsForRole(role)));
        return JSON.toJSONString(result);
    }

    /**
     * 可分配的权限元素树（扁平 id/pid/name，前端组树）+ 当前已选
     */
    @RequestMapping(value = "/{roleId}/elements", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String elements(@PathVariable("roleId") Integer roleId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TSysRole role = Cayenne.objectForPK(context, TSysRole.class, roleId);
        if (role == null) return notFound("角色不存在");

        Map<String, Object> result = new LinkedHashMap<>();
        List elementList = sysRoleService.getElementAllBySystemAdmin(role);
        result.put("elements", elementList == null ? Collections.emptyList() : elementList);
        result.put("checked", parseIdList(sysRoleService.getElementIdsForRole(role)));
        return JSON.toJSONString(result);
    }

    /**
     * 新建角色
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "roleDesc", required = false) String roleDesc) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(roleName)) return errorJson("请填写角色名称");

        try {
            return JSON.toJSONString(sysRoleService.addRole(roleName, roleDesc, user).toMap());
        } catch (Exception e) {
            logger.error("Create role failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑角色
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String edit(
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "roleDesc", required = false) String roleDesc) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (roleId == null || StringUtil.isEmpty(roleName)) return errorJson("参数不完整");

        try {
            return JSON.toJSONString(sysRoleService.editRole(roleId, roleName, roleDesc, user).toMap());
        } catch (Exception e) {
            logger.error("Edit role failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 绑定权限元素
     */
    @RequestMapping(value = "/bindElements", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String bindElements(
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "elementIds", required = false) String elementIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (roleId == null) return errorJson("参数不完整");

        try {
            ObjectContext context = user.getObjectContext();
            TSysRole role = Cayenne.objectForPK(context, TSysRole.class, roleId);
            boolean ok = sysRoleService.bindElement(role, parseIdList(elementIdsCsv == null ? "" : elementIdsCsv), user);
            if (ok) {
                Map<String, Object> okMap = new LinkedHashMap<>();
                okMap.put("code", 0);
                okMap.put("type", "success");
                okMap.put("message", "保存成功");
                return JSON.toJSONString(okMap);
            }
            return errorJson("保存失败");
        } catch (Exception e) {
            logger.error("Bind elements failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的角色");

        try {
            return JSON.toJSONString(sysRoleService
                    .deleteRoles(user.getObjectContext(), parseIdList(idsCsv)).toMap());
        } catch (Exception e) {
            logger.error("Delete role failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static String roleTypeName(Integer roleType) {
        if (roleType == null) return "-";
        switch (roleType) {
            case TSysRole.ROLE_TYPE_SYSTEM: return "系统";
            case TSysRole.ROLE_TYPE_PUBLIC: return "公共";
            case TSysRole.ROLE_TYPE_COMPANY: return "公司";
            default: return "-";
        }
    }

    private static List<Integer> parseIdList(String csv) {
        List<Integer> ids = new ArrayList<>();
        if (StringUtil.isEmpty(csv)) return ids;
        for (String part : csv.split(",")) {
            if (!part.trim().isEmpty()) ids.add(Integer.parseInt(part.trim()));
        }
        return ids;
    }

    private static String notFound(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 404);
        err.put("message", message);
        return JSON.toJSONString(err);
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
