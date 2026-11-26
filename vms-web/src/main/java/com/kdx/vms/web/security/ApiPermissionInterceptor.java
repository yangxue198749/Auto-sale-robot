package com.kdx.vms.web.security;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysElement;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST 操作权限校验：只管标注了 {@link RequirePermission} 的端点，其余端点行为不变。
 *
 * 背景：/vms/api/** 在 Shiro 链中为 anon，各 ApiController 自行 requireAuth() 只校验"已登录"；
 * 旧 SSOAccessControlFilter 按 URL 查权限码的机制不覆盖 REST。结果任何能登录的账号都能
 * 调用角色绑定权限、重置密码等管理接口。
 *
 * 权限口径沿用旧系统 RBAC：用户 -> T_SYS_USER_ROLE -> T_SYS_ROLE_ELEMENT -> ELEMENT_CODE。
 * 不走 Shiro 的 isPermitted：REST 登录路径不经 /toloadMenu，SSOToken.authInfo 可能为空。
 * 每次请求实查数据库、不缓存——权限撤销即时生效；查询失败一律拒绝（fail-closed）。
 */
public class ApiPermissionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ApiPermissionInterceptor.class);

    private static final String NO_PERMISSION = "没有操作权限";

    private static final String PERMISSION_SQL =
            "SELECT DISTINCT e.ELEMENT_CODE FROM T_SYS_USER_ROLE ur"
            + " JOIN T_SYS_ROLE_ELEMENT re ON re.ROLE_ID = ur.ROLE_ID"
            + " JOIN T_SYS_ELEMENT e ON e.ELEMENT_ID = re.ELEMENT_ID"
            + " WHERE ur.USER_ID = #bind($userId) AND e.ELEMENT_CODE IS NOT NULL";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;
        RequirePermission required = ((HandlerMethod) handler).getMethodAnnotation(RequirePermission.class);
        if (required == null) return true;

        String method = request.getMethod();
        String uri = request.getRequestURI();

        Subject subject;
        try {
            subject = SecurityUtils.getSubject();
        } catch (Exception e) {
            logger.error("[api-permission] DENIED {} uri={} reason=no-security-context", method, uri, e);
            return deny(response, 403, NO_PERMISSION);
        }
        if (!subject.isAuthenticated() || !(subject.getPrincipal() instanceof SSOToken)) {
            return deny(response, 401, "未登录");
        }

        SSOToken token = (SSOToken) subject.getPrincipal();
        TSysUser user = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        if (user == null || context == null) {
            logger.error("[api-permission] DENIED {} uri={} reason=no-user-context", method, uri);
            return deny(response, 403, NO_PERMISSION);
        }
        // 标注了却没写权限码属于配置错误，按 fail-closed 拒绝而不是放行
        if (required.value().length == 0) {
            logger.error("[api-permission] DENIED {} uri={} userId={} reason=empty-annotation", method, uri, user.getId());
            return deny(response, 403, NO_PERMISSION);
        }

        Set<String> owned;
        try {
            owned = loadPermissionCodes(context, user.getId());
        } catch (Exception e) {
            logger.error("[api-permission] DENIED {} uri={} userId={} reason=lookup-failed",
                    method, uri, user.getId(), e);
            return deny(response, 403, NO_PERMISSION);
        }

        for (String code : required.value()) {
            if (owned.contains(code)) {
                logger.info("[api-permission] ALLOWED {} uri={} userId={} account={} via={}",
                        method, uri, user.getId(), user.getUserAccount(), code);
                return true;
            }
        }

        logger.warn("[api-permission] DENIED {} uri={} userId={} account={} required={}",
                method, uri, user.getId(), user.getUserAccount(), java.util.Arrays.asList(required.value()));
        return deny(response, 403, NO_PERMISSION);
    }

    /** 当前用户经角色获得的全部权限码。 */
    static Set<String> loadPermissionCodes(ObjectContext context, int userId) {
        // SQLTemplate 默认返回 DataRow（非实体），不进对象缓存，读到的一定是库里的最新绑定
        SQLTemplate query = new SQLTemplate(TSysElement.class, PERMISSION_SQL);
        query.setParameters(Collections.singletonMap("userId", userId));
        query.setFetchingDataRows(true);
        List<?> rows = context.performQuery(query);
        Set<String> codes = new LinkedHashSet<>();
        if (rows == null) return codes;
        for (Object row : rows) {
            if (row instanceof Map) {
                for (Object value : ((Map<?, ?>) row).values()) {
                    if (value != null) codes.add(String.valueOf(value));
                }
            }
        }
        return codes;
    }

    /** 写出与各 ApiController 一致的错误 JSON（{"code":..,"message":..}）并中断请求。 */
    private static boolean deny(HttpServletResponse response, int status, String message) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", status);
        body.put("message", message);
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(JSON.toJSONString(body));
        return false;
    }
}
