package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TMtWarehouse;

public class TMtWarehouse extends _TMtWarehouse {

	public Integer getId() {
		return Cayenne.intPKForObject(this);
	}
}
