package com.kdx.vms.service;

import java.util.List;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtCompany;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;

public interface ISysUserService {

	/**
	 * 更新用户密码
	 */
	Boolean updateUserPwd(ObjectContext context, TSysUser tSysUser, String pwd);
	
	/**
	 * 更新用户名字
	 */
	Boolean updateUserName(ObjectContext context, TSysUser tSysUser, String userName);
	
	/**
	 * 更新用户手机号
	 */
	Boolean updateUserPhone(ObjectContext context, TSysUser tSysUser, String userPhone);
	
	/**
	 * 精确查找用户账号
	 */
	TSysUser getUserByAccount(ObjectContext context, String account);

	TSysUser getAliveUserByAccount(ObjectContext context,String account);
	
	/**
	 * 根据名字，查询用户
	 */
	List<TSysUser> getTSysUserByName(String userName, ObjectContext context);
	
	/**
	 * 判断是不是管理员
	 */
	Boolean isAdminUser(ObjectContext context, TSysUser tSysUser);

    List getSysUserForCompany(ObjectContext context, String searchType, String searchValue, Integer mtCompanyId, Integer pageSize);

    List getSysUserForCompany(ObjectContext context, String searchType, String searchValue, TMtCompany mtCompanyId, Integer pageSize);

    SystemCode createUser(String userAccount, String userName, String userPhone, Integer companyId, List<Integer> roleIdList, TSysUser currUser);

    SystemCode updateUser(Integer userId, String userName, String userPhone, Integer companyId, List<Integer> roleIdList, TSysUser currUser);

    SystemCode deleteUser(List<Integer> userIdList, TSysUser currUser);

	List<TSysUser> findSysUsersByRoleLevelForCompany(ObjectContext context, String userName, Integer roleLevel, Integer companyId);
	/**
	 * 查询同一公司下某一级别用户
	 * @param context
	 * @param roleLevel 角色级别
	 * @param companyId 公司标识
	 * @param defaultUserId 默认选中
	 * @return 返回的为页面select元素
	 */
	String findSysUsersByRoleLevelForCompany(ObjectContext context,Integer roleLevel,Integer companyId,Integer defaultUserId);

	/**
	 * 查询公司下所有用户
	 * @param company
	 * @return
	 */
	String findSysUsersByCompany(TMtCompany company);

	/**
	 * 刷新角色，权限相关缓存
	 * @param user
	 */
	void flashRoleCache(TSysUser user);

    boolean resetPassword(Integer ids, TSysUser currUser);

}
