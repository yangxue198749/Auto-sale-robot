package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 16-12-1.
 */
public interface ISysRoleService {

    /**
     * 获取公司及公共角色
     * @param company
     * @return
     */
    List getSysRoleCompany(TMtCompany company, String roleName, Integer pageSize);

    SystemCode addRole(String roleName, String roleDesc, TSysUser currUser);

    SystemCode editRole(Integer id, String roleName, String roleDesc, TSysUser currUser);

    SystemCode deleteRole(ObjectContext context, Integer userId);

    SystemCode deleteRoles(ObjectContext context, List<Integer> userIds);

    Boolean bindElement(TSysRole sysRole, List<Integer> ids, TSysUser currUser);

    List<Map<String, Object>> getElementAll(TSysRole role);

    public TSysRole getAdminRole(ObjectContext context , Integer level);
    
    public String getElementIdsForRole(TSysRole role);

    /**
     * 获取当前角色所属公司的最大权限范围
     * @param sysRole
     * @return
     */
	List getElementAllBySystemAdmin(TSysRole sysRole);

	/**
	 * 查询当前公司下角色，不包含下属公司角色
	 * @param company
	 * @return
	 */
	List getSysRolByCompany(TMtCompany company);


}
