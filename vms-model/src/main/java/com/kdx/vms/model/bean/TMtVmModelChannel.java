package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TMtVmModelChannel;

public class TMtVmModelChannel extends _TMtVmModelChannel {
	
	private Integer id;
	
	public Integer getId(){
		if(id != null) return id;
		return Cayenne.intPKForObject(this);
	}

	public void setId(Integer id){
		this.id = id;
	}
}
