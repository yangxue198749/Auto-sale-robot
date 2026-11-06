package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TMtArea;

public class TMtArea extends _TMtArea {
	
	public Integer getId(){
		return Cayenne.intPKForObject(this);
	}

}
