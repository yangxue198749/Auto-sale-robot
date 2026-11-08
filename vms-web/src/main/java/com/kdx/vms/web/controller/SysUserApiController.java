package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserRole;
import com.kdx.vms.service.ISysRoleService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户管理 REST API
 */
@Controller
@RequestMapping("/vms/api/sysUser")
public class SysUserApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysUserApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private ISysUserService sysUserService;

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
     * 用户列表（当前公司范围）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "selectType", required = false) String selectType,
            @RequestParam(value = "selectText", required = false) String selectText) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        List full = sysUserService.getSysUserForCompany(context, selectType, selectText,
                user.getCompany().getId(), MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = full.get(i);
            if (obj instanceof TSysUser) {
                rows.add(convertUser((TSysUser) obj));
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
     * 用户详情
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("userId") Integer userId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        TSysUser target = Cayenne.objectForPK(user.getObjectContext(), TSysUser.class, userId);
        if (target == null) return notFound("用户不存在");
        return JSON.toJSONString(convertUser(target));
    }

    /**
     * 可分配角色（当前公司）
     */
    @RequestMapping(value = "/roleOptions", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String roleOptions() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        List roles = sysRoleService.getSysRolByCompany(user.getCompany());
        if (roles != null) {
            for (Object obj : roles) {
                if (obj instanceof TSysRole) {
                    TSysRole r = (TSysRole) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", r.getId());
                    row.put("roleName", r.getRoleName());
                    rows.add(row);
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 新建用户（初始密码与账号同旧逻辑，由服务层处理）
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "userAccount", required = false) String userAccount,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "userPhone", required = false) String userPhone,
            @RequestParam(value = "roleIds", required = false) String roleIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(userAccount) || StringUtil.isEmpty(userName)) {
            return errorJson("请填写账号和姓名");
        }

        try {
            return JSON.toJSONString(sysUserService.createUser(userAccount, userName, userPhone,
                    user.getCompany().getId(), parseIdList(roleIdsCsv), user).toMap());
        } catch (Exception e) {
            logger.error("Create user failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑用户
     */
    @RequestMapping(value = "/{userId}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("userId") Integer userId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "userPhone", required = false) String userPhone,
            @RequestParam(value = "roleIds", required = false) String roleIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (userId == null || StringUtil.isEmpty(userName)) return errorJson("参数不完整");

        try {
            return JSON.toJSONString(sysUserService.updateUser(userId, userName, userPhone,
                    user.getCompany().getId(), parseIdList(roleIdsCsv), user).toMap());
        } catch (Exception e) {
            logger.error("Update user failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的用户");

        try {
            return JSON.toJSONString(sysUserService.deleteUser(parseIdList(idsCsv), user).toMap());
        } catch (Exception e) {
            logger.error("Delete user failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "/{userId}/resetPwd", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String resetPwd(@PathVariable("userId") Integer userId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (userId == null) return errorJson("参数不完整");

        try {
            if (sysUserService.resetPassword(userId, user)) {
                Map<String, Object> ok = new LinkedHashMap<>();
                ok.put("code", 0);
                ok.put("type", "success");
                ok.put("message", "重置密码成功");
                return JSON.toJSONString(ok);
            }
            return errorJson("重置密码失败");
        } catch (Exception e) {
            logger.error("Reset password failed", e);
            return errorJson("重置失败：" + e.getMessage());
        }
    }

    private static Map<String, Object> convertUser(TSysUser u) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", u.getId());
        row.put("userAccount", u.getUserAccount());
        row.put("userName", u.getUserName());
        row.put("userPhone", u.getUserPhone());
        row.put("lockMark", u.getLockMark());
        row.put("deleteMark", u.getDeleteMark());
        row.put("loginTime", u.getLoginTime() != null ? u.getLoginTime().toInstant().toString() : null);
        if (u.getCompany() != null) {
            row.put("companyId", u.getCompany().getId());
            row.put("companyName", u.getCompany().getCompanyName());
        }
        List<Integer> roleIds = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        List<TSysUserRole> userRoles = u.getUserRole();
        if (userRoles != null) {
            for (TSysUserRole ur : userRoles) {
                TSysRole role = ur.getRole();
                if (role != null) {
                    roleIds.add(role.getId());
                    roleNames.add(role.getRoleName());
                }
            }
        }
        row.put("roleIds", roleIds);
        row.put("roleNames", roleNames);
        return row;
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
