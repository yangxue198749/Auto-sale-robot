package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
import java.util.List;

/**
 * 售货机配置信息
 * @author zhangguodong
 *
 */
public class DeviceInfo implements Serializable{

	private static final long serialVersionUID = 7899973916223255165L;
	/**
	 * 闪购键数量
	 */
	private Integer buttonCount;
	/**
	 * 各货道层数
	 */
	private String[] channelCount;
	/**
	 * 版本信息
	 */
	private List<VersionInfo> versionInfo;
	public String[] getChannelCount() {
		return channelCount;
	}
	public void setChannelCount(String[] channelCount) {
		this.channelCount = channelCount;
	}
	public List<VersionInfo> getVersionInfo() {
		return versionInfo;
	}
	public void setVersionInfo(List<VersionInfo> versionInfo) {
		this.versionInfo = versionInfo;
	}
	public Integer getButtonCount() {
		return buttonCount;
	}
	public void setButtonCount(Integer buttonCount) {
		this.buttonCount = buttonCount;
	}
}
