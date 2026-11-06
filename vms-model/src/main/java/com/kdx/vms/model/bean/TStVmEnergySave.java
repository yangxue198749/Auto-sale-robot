package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TStVmEnergySave;

public class TStVmEnergySave extends _TStVmEnergySave {

	/**
	 * 制冷
	 */
	public final static int REFRIGERATION = 1;
	/**
	 * 制热
	 */
	public final static int HEATING = 2;
	/**
	 * 屏幕节能
	 */
	public final static int SCREEN = 3;
	/**
	 * 常温
	 */
	public final static int HOMOIOTHERMY = 4;


	public static final int TEMP_MAX = 50;
	public static final int TEMP_MIN = 0;
	
	public Integer getEnergySaveId(){
		return Cayenne.intPKForObject(this);
	}
	
}
