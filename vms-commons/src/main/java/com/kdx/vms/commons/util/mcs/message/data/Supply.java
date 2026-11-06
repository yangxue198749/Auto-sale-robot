package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

public class Supply implements Serializable{

	private static final long serialVersionUID = -1402863072455521145L;
	private String channelCode;
	
	private Integer currentCount;

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public Integer getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
	}
	
}
