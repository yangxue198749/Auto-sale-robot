package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysUser;

public interface IMtCustomerService {
	
	/**
	 * 新增客户
	 */
	public Integer addCustomer(TMtCustomer tMtCustomer ,Integer userId ,ObjectContext context);
	
	/**
	 * 查看客户
	 */
	public Map<String, Object> list(String customerName ,String manageName ,String customerMode ,String customerType ,Integer pageSize ,Integer pageNo ,TSysUser user ,ObjectContext context);
	
	/**
	 * 更新客户
	 */
	public Integer updateCustomer(TMtCustomer tMtCustomer ,Integer userId ,ObjectContext context);
	
	/**
	 * 删除客户
	 */
	public Boolean delCustomer(String[] ids ,ObjectContext context);
	
	/**
	 * 得到未使用的客户
	 */
	public List<TMtCustomer> getUnusedTMtCustomer(TSysUser user ,ObjectContext context);
	
	/**
	 * 根据id得到客户
	 */
	public TMtCustomer getTMtCustomerById(Integer id ,ObjectContext context);

}
