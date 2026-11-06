package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TMtVmEnergySave;

public class TMtVmEnergySave extends _TMtVmEnergySave {

	public Integer getEnergySaveId(){
		return Cayenne.intPKForObject(this);
	}
}
