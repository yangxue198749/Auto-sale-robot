package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TSysRole;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 16-11-28.
 */
public interface SysElementDao {
	
	/**
	 * 根据访问路径获取资源编码
	 */
	String getCodeByUrl(ObjectContext context,String url);

    List getSysElementAll(ObjectContext context);

    /**
     * 获取角色所有元素
     * @param role
     * @return
     */
    List getSysElementByRole(TSysRole role);

    /**
     * 获取当前角色所属公司的最大权限范围
     * @param sysRole
     * @return
     */
	List getElementAllBySystemAdmin(TSysRole sysRole);

}
