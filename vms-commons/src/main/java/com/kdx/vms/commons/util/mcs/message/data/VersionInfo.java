package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 * 版本信息
 * @author zhangguodong
 *
 */
public class VersionInfo implements Serializable{

	private static final long serialVersionUID = 5717647975082074862L;
	private String version;
	private Integer type;

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
