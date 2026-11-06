package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TSysElement;

public class TSysElement extends _TSysElement {

	public static final String MODULE = "module"; 
	public static final String MENU = "menu"; 
	public static final String BUTTON = "button"; 
	public static final String LITTLE_MODULE = "little-module"; 
	public static final Integer VMS_MODULE = 0;
	public static final Integer MP_MODULE = 1;
	
	public Integer getEleMentId(){
		return Cayenne.intPKForObject(this);
	}

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    public Integer getPid() {
        TSysElement sysElement = getParentElement();
        return null == sysElement ? 0 : Cayenne.intPKForObject(sysElement);
    }



}
