package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.IntegerUtils;
import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TStVmChannel;

public class TStVmChannel extends _TStVmChannel {

	/**
	 * 是固定容量
	 */
	public final static int IS_FIXED_CAPACITY = 1;
	
	/**
	 * 摆放方式，横放
	 */
	public final static int HORIZONTAL = 1;
	/**
	 * 竖放
	 */
	public final static int VERTICAL = 0;
	public int getChannelId(){
		return  Cayenne.intPKForObject(this);
	}

	public Integer getTempMaxCapacity() {

		if(IntegerUtils.isPositiveInteger(super.getAdjustMaxCapacity()))
			return super.getAdjustMaxCapacity();
		if(null!=getIsFixedCapacity()&&getIsFixedCapacity()==TStVmChannel.IS_FIXED_CAPACITY)
			return getFixedCapacity();
		return getMaxCapacity();
	}
}
