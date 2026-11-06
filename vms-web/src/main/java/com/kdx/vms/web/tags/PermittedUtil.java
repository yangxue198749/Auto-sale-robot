package com.kdx.vms.web.tags;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

public class PermittedUtil {

	public static Boolean isPermitted(String code){
		
		Subject subject = SecurityUtils.getSubject();
		SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
		if (ss == null) return false;
		
		return subject.isPermitted(code);
	}
}
