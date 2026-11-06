package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;
import org.apache.commons.lang3.StringUtils;

import com.kdx.vms.model.bean.auto._TMtCustomer;

public class TMtCustomer extends _TMtCustomer {

    public static final int CUSTOMER_TYPE_B = 2;
	
	private Integer id;
	
	public Integer getId() {
		if(id != null) return id;
		return Cayenne.intPKForObject(this);
	}
	
	public void setId(Integer id){
		this.id = id ;
	}
	
	public void setProperties(TMtCustomer mc){
		if(StringUtils.isNotEmpty(mc.getCustomerName()))
			setCustomerName(mc.getCustomerName());
		if(mc.getCustomerType() != null )
			setCustomerType(mc.getCustomerType());
		if(StringUtils.isNotEmpty(mc.getCustomerAddress()))
			setCustomerAddress(mc.getCustomerAddress());
		if(StringUtils.isNotEmpty(mc.getManagerName()))
			setManagerName(mc.getManagerName());
		if(StringUtils.isNotEmpty(mc.getManagerPhone()))
			setManagerPhone(mc.getManagerPhone());
		if(StringUtils.isNotEmpty(mc.getCollectionAccount()))
			setCollectionAccount(mc.getCollectionAccount());
		if(StringUtils.isNotEmpty(mc.getCollectionAccountType()))
			setCollectionAccountType(mc.getCollectionAccountType());
		if(StringUtils.isNotEmpty(mc.getCustomerEmail()))
			setCustomerEmail(mc.getCustomerEmail());
		if(StringUtils.isNotEmpty(mc.getTaxCode()))
			setTaxCode(mc.getTaxCode());
		
		if(StringUtils.isNotEmpty(mc.getGiroAccount()))
			setGiroAccount(mc.getGiroAccount());
		if(StringUtils.isNotEmpty(mc.getGiroAccountName()))
			setGiroAccountName(mc.getGiroAccountName());
		if(mc.getGiroAccountType() != null)
			setGiroAccountType(mc.getGiroAccountType());
	}

}
