package com.kdx.vms.web.oauth;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysUserService;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

public class SSORealm extends AuthorizingRealm {

	@Resource
	private ISysUserService sysUserService;
	public SSORealm() {
		super();
		this.setAuthenticationTokenClass(SSOToken.class);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {
		SSOToken ssoToken = (SSOToken) collection.fromRealm(getName()).iterator().next();
		SimpleAuthorizationInfo info = ssoToken.getAuthInfo();
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		SSOToken token = new SSOToken();
		if(authenticationToken instanceof SSOToken){
			token = (SSOToken) authenticationToken;
		}else{
			throw new AuthenticationException("认证请求参数有误，请核实");
		}
		//根据账号查询用户信息
		
		ObjectContext context = token.getDempObjectContext();
		TSysUser user = sysUserService.getAliveUserByAccount(context, token.getUsername());
		final String password ;
		if(user == null){//账号不对
			password = "";
		}else{
			password = user.getUserPassword();//密码
			token.setUserId(user.getId());
			token.setUserName(user.getUserName());
			token.setDempObjectContext(context);
		}
		
		return new SimpleAuthenticationInfo(token, password.toCharArray() , getName());
	}
	
	
}