package com.kdx.vms.web.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注端点所需的操作权限码（T_SYS_ELEMENT.ELEMENT_CODE，与 menu-vms.md 登记一致）。
 *
 * 由 ApiPermissionInterceptor 统一校验；多个码为"任一满足"。
 * 未标注的端点不受影响（仍只要求登录）。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    String[] value();
}
