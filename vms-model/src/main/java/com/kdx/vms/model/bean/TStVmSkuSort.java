package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TStVmSkuSort;

public class TStVmSkuSort extends _TStVmSkuSort {

	public Integer getSortId(){
		return Cayenne.intPKForObject(this);
	}
}
