package com.kdx.vms.commons.util.mcs.message;

import java.io.Serializable;

/**
 * 返回结果
 * @author zhangguodong
 *
 */
public class MessageResult implements Serializable{
	private static final long serialVersionUID = -6185387888033179480L;
	private String from;
	private String to;
	private String sn;
	/**
	 * 0 代表成功
	 */
	private Integer success;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getSuccess() {
		return success;
	}
	public void setSuccess(Integer success) {
		this.success = success;
	}
	
}
