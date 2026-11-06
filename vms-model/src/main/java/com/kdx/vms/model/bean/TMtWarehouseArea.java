package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TMtWarehouseArea;

public class TMtWarehouseArea extends _TMtWarehouseArea {
	
	public Integer getId() {
		return Cayenne.intPKForObject(this);
	}

}
