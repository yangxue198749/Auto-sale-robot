package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtPartner;
import org.apache.cayenne.Cayenne;

public class TMtPartner extends _TMtPartner {
	
	private Integer id;
	
	public Integer getId(){
		if(id != null) return id;
		return Cayenne.intPKForObject(this);
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	
	
	public void setProperties(TMtPartner tMtPartner){
		setPartnerName(tMtPartner.getPartnerName());
		setPartnerType(tMtPartner.getPartnerType());
		setPartnerAddress(tMtPartner.getPartnerAddress());
		setManagerName(tMtPartner.getManagerName());
		setManagerPhone(tMtPartner.getManagerPhone());
		setCollectionAccount(tMtPartner.getCollectionAccount());
		setCollectionAccountType(tMtPartner.getCollectionAccountType());
		setPartnerEmail(tMtPartner.getPartnerEmail());
	}
	
}
