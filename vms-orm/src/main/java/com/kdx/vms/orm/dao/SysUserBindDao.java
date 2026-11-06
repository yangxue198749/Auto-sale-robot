package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TSysUserBind;

/**
 * Created by liyang on 16-11-28.
 */
public interface SysUserBindDao {
    public TSysUserBind findUserBindByOuterId(String outerId,Integer bindType);
}
