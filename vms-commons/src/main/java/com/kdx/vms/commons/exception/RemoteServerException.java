package com.kdx.vms.commons.exception;
/**
 * 异常
 * @author zhangguodong
 *
 */
public class RemoteServerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;

	public RemoteServerException(String message) {
		super(message);
	}

	public RemoteServerException(Throwable cause) {
		super(cause.getMessage());	
		if(cause instanceof RemoteServerException){
			RemoteServerException exception  = (RemoteServerException)cause;
			this.code = exception.getCode();
		}
	}

	public RemoteServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteServerException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
