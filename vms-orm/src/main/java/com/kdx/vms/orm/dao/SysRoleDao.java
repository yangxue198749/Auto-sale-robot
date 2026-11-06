package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysRole;

import java.util.List;

import org.apache.cayenne.ObjectContext;

/**
 *
 * Created by liyang on 16-11-28.
 */
public interface SysRoleDao {


    /**
     * 获取公司及公共角色
     * @param company
     * @return
     */
    List findSysRoleCompany(TMtCompany company, String roleName, Integer pageSize);
    
    public TSysRole findAdminRole(ObjectContext context, int level ,int type);
    
    
    /**
     * 查询当前公司下角色，不包含下属公司角色
     * @param company
     * @return
     */
    List findSysRoleByCompany(TMtCompany company);

}
