package com.kdx.vms.orm.dao;

import java.util.List;

import com.kdx.vms.model.bean.TMtCompany;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;

/**
 * .
 * Created by liyang on 16-11-28.
 */
public interface SysUserDao {
	
	public List findSysUserByAccount(ObjectContext context, String account,boolean eliminateDelete);

	public boolean addUser(TSysUser tSysUser, ObjectContext context);
	
	public List findSysUserByName(ObjectContext context, String name);

    /**
     * 根据公司不同级别，返回不同的用户结果集
     * @param context     context
     * @param searchType  查询类型 "1"-用户名 "2"-公司名
     * @param searchValue 查询值
     * @param mtCompany   公司对象
     * @return 用户结果集
     */
    public List findSysUserForCompany(ObjectContext context, String searchType, String searchValue, TMtCompany mtCompany, Integer pageSize);

    /**
     * 根据公司不同级别，返回不同的用户结果集
     * @param context     context
     * @param searchType  查询类型 "1"-用户名 "2"-公司名
     * @param searchValue 查询值
     * @param mtCompanyId 公司id
     * @return 用户结果集
     */
    public List findSysUserForCompany(ObjectContext context, String searchType, String searchValue, Integer mtCompanyId, Integer pageSize);

    public List<TSysUser> findSysUsersByRoleLevelForCompany(ObjectContext context, String userName, Integer roleLevel, Integer companyId);

    /**
     * 根据主键查询用户
     * @param context
     * @return
     */
	public TSysUser findUserByPk(ObjectContext context, Integer userId);

	/**
	 * 刷新角色，权限相关缓存
	 * @param user
	 */
	public void flashRoleCache(TSysUser user);

}
