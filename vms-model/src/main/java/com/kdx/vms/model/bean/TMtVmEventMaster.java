package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtVmEventMaster;
import org.apache.cayenne.Cayenne;

public class TMtVmEventMaster extends _TMtVmEventMaster {
	/**
	 * 整机促销
	 */
	public final static Integer EVENT_TYPE_VM = 1;
	/**
	 * 商品促销
	 */
	public final static Integer EVENT_TYPE_SKU = 2;
    public Integer getId(){
        return Cayenne.intPKForObject(this);
    }
}
