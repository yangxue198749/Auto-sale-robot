package com.kdx.vms.web.security;

import com.kdx.vms.model.bean.TSysElement;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysRoleElement;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserRole;
import com.kdx.vms.service.ISysUserService;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * 登录时装配 Shiro 授权信息（角色 + 权限码）。
 *
 * 装配逻辑与 HomeController.toloadMenu 一致（后者保留为兜底：仅当 authInfo 为空时装载）。
 * 在登录成功路径调用后，授权信息随会话生效：
 *   - /auth/me 返回真实 roles / permissions（前端据此渲染菜单）
 *   - 旧 SSOAccessControlFilter 的 isPermitted 对 REST 会话可用（消除"管理员被旧过滤器拒"的不对称）
 *   - REST 写接口的操作权限校验不经过本缓存（ApiPermissionInterceptor 每次实查数据库）
 *
 * 角色变更需重新登录生效——与旧 JSP 的会话级装载行为一致。
 */
public final class AuthInfoLoader {

    private AuthInfoLoader() {
    }

    /**
     * 装配当前用户的角色集合与权限码集合。
     * 无角色用户返回空的授权信息（非 null）：isPermitted 恒 false，语义明确。
     */
    public static SimpleAuthorizationInfo build(TSysUser user, ISysUserService sysUserService) {
        Set<String> roleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        if (user != null && sysUserService != null) {
            sysUserService.flashRoleCache(user);
            java.util.List<TSysUserRole> roleList = user.getUserRole();
            if (roleList != null) {
                for (TSysUserRole userRole : roleList) {
                    TSysRole role = userRole == null ? null : userRole.getRole();
                    if (role == null) continue;
                    roleSet.add(String.valueOf(role.getId()));
                    java.util.List<TSysRoleElement> elements = role.getRoleElement();
                    if (elements == null) continue;
                    for (TSysRoleElement roleElement : elements) {
                        TSysElement element = roleElement == null ? null : roleElement.getElement();
                        if (element != null && element.getElementCode() != null && element.getElementCode().length() > 0) {
                            permissionSet.add(element.getElementCode());
                        }
                    }
                }
            }
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roleSet);
        info.addStringPermissions(permissionSet);
        return info;
    }
}
