package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TSysUser;

public interface IMtPartnerService {
	
	/**
	 * 查看合伙人
	 */
	public Map<String, Object> list(String partnerName ,String manageName ,String partnerType ,Integer pageSize ,Integer pageNo ,TSysUser user ,ObjectContext context);

	/**
	 * 更新合伙人
	 */
	public Integer updatePartner(TMtPartner tMtPartner ,Integer userId ,ObjectContext context);
	
	/**
	 * 删除合伙人
	 */
	public Boolean delPartner(String[] ids ,ObjectContext context);
	
	/**
	 * 得到未使用的合伙人
	 */
	public List<TMtPartner> getUnusedTMtPartner(TSysUser user ,ObjectContext context);
	
	/**
	 * 根据id得到合伙人
	 */
	public TMtPartner getTMtCustomerById(Integer id ,ObjectContext context);
	
	/**
	 * 新增合伙人
	 */
	public Integer addPartner(TMtPartner tMtPartner ,TSysUser user ,ObjectContext context);
}
