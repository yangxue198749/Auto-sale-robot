package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TSysUserBind;

public class TSysUserBind extends _TSysUserBind {

    public static final int BIND_TYPE_WECHAT = 1;

    @Override
    public TSysUser getUser() {
        TSysUser sysUser = super.getUser();
        return null == sysUser || sysUser.getDeleteMark() != TSysUser.DELETE_MARK_EXIST ? null : sysUser;
    }
}
