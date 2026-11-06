package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TSysRole;

public class TSysRole extends _TSysRole {

    /** 系统内部 */
    public static final int ROLE_TYPE_SYSTEM  = 1;
    /** 系统预设、公共 */
    public static final int ROLE_TYPE_PUBLIC  = 2;
    /** 自定义、用户创建 */
    public static final int ROLE_TYPE_COMPANY = 3;

    /** 系统管理员（一级公司） */
    public static final int ROLE_LEVEL_ADMIN    = 1;
    /** 系统管理员（二级公司） */
    public static final int ROLE_LEVEL_ADMIN_2  = 2;
    /** 系统管理员（三级公司） */
    public static final int ROLE_LEVEL_ADMIN_3  = 3;
    /** 补货员 */
    public static final int ROLE_LEVEL_DELIVERY = 4;
    /** 维修员 */
    public static final int ROLE_LEVEL_REPAIR   = 5;
    /**
     * 魔盒运营
     */
    public static final int ROLE_LEVEL_MP_OPERATE   = 6;
    /**
     * 魔盒报表
     */
    public static final int ROLE_LEVEL_MP_DATA   = 7;
    
    /** 其他 */
    public static final int ROLE_LEVEL_OTHER    = 99;

    public Integer getId(){
    	return Cayenne.intPKForObject(this);
    }

}
