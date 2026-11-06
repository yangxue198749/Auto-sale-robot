package com.kdx.vms.web.oauth;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SSOToken extends UsernamePasswordToken {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String  userName;
	private Date lastLoginTime;
	
	/**
	 * 失败次数
	 */
	private Integer failCount;
	/**
	 * 锁定状态
	 */
	private Integer lockMark;
	private ObjectContext objectContext;
	
	/**
     * 认证信息
     */
	private SimpleAuthorizationInfo authInfo;
	public SSOToken() {}
	public SSOToken(final String username, final char[] password) {
		super(username, password);
	}

	public SSOToken(final String username, final String password) {	
		super(username, password);
	}

	public SSOToken(final String username, final char[] password,final String host) {
		super(username, password, host);
	}

	public SSOToken(final String username, final String password,final String host) {
		super(username, password, host);
	}

	public SSOToken(final String username, final char[] password, final boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public SSOToken(final String username, final String password,final boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public SSOToken(final String username, final char[] password,final boolean rememberMe, final String host) {
		super(username, password, rememberMe, host);
	}

	public SSOToken(final String username, final String password, final boolean rememberMe, final String host) {
		super(username, password, rememberMe, host);
	}
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public SimpleAuthorizationInfo getAuthInfo() {
		return authInfo;
	}
	public void setAuthInfo(SimpleAuthorizationInfo authInfo) {
		this.authInfo = authInfo;
	}

	public ObjectContext getObjectContext() {
		try {
			return getSysUser().getObjectContext();
		}catch (Exception e){
			logger.error("UserMap.getUser error " + e.getLocalizedMessage(),e);
		}
		return null;
	}
	public void setDempObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}
	public ObjectContext getDempObjectContext() {
		return this.objectContext;
	}
	public Integer getFailCount() {
		return failCount;
	}
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	public Integer getLockMark() {
		return lockMark;
	}
	public void setLockMark(Integer lockMark) {
		this.lockMark = lockMark;
	}

    public TSysUser getSysUser() {
		try {
			return UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
		}catch (Exception e){
			logger.error("UserMap.getUser error " + e.getLocalizedMessage(),e);
		}
		return null;
    }
}
