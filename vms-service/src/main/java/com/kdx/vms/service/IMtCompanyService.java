package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

public interface IMtCompanyService {

	/**
	 * 创建三级公司
	 */
	public Map<String, Object> addCompany(String companyName, String customerId, String adminName, String adminAccount,
			String userPhone, Integer maxVmCapacity, TSysUser user, ObjectContext context);
	
	/**
	 * 升级公司,同时升级管理员
	 * @param companyName
	 * @param user
	 * @param context
	 * @return SystemCode
	 * @date 2016年12月1日 下午7:40:00
	 * @author chenifan
	 */
	public Map<String, Object> upgradeCompany(String companyName ,String newAdminUserId ,TSysUser user ,ObjectContext context);
	
	/**
	 * 二级公司或三级公司用户查询公司列表
	 * @param companyName 公司名字
	 * @param customerName 客户名字
	 * @param adminName 管理员名字
	 * @param pageSize 分页大小
	 * @param pageNo 当前页
	 * @param user 用户
	 * @param context 
	 * @return Map<String,Object>
	 * @date 2016年12月2日 下午2:08:08
	 * @author chenifan
	 */
	public Map<String, Object> list(String companyName ,String customerName ,String adminName ,Integer pageSize ,Integer pageNo ,TSysUser user ,ObjectContext context);
	
	/**
	 * 更新公司名字
	 * @param companyId 公司id
	 * @param companyName 公司名字
	 * @param user
	 * @param context
	 * @return Boolean
	 * @author chenifan
	 */
	public Integer updateCompany(String companyId ,String companyName ,TSysUser user ,ObjectContext context);
	
	/**
	 * 分配售货机
	 * @param companyId
	 * @param maxVmCapacity
	 * @param user
	 * @param context
	 * @return Boolean
	 */
	public Boolean addCapacity(String companyId , Integer maxVmCapacity ,TSysUser user ,ObjectContext context);
	
	public TMtCompany getTMtCompanyById(Integer companyId ,ObjectContext context);

	public Map<String,Object> exportOrUnStock(String id,ObjectContext context);

	/**
	 * 查询公司列表
	 * @param companyName
	 * @param company
	 * @param pageSize
	 * @param context
	 * @return
	 */
	List<TMtCompany> findCompanyPage(String companyName, TMtCompany company  , Integer pageSize , ObjectContext context);

}
