package com.kdx.vms.web.tags;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import com.kdx.vms.orm.dao.SysElementDao;
import com.kdx.vms.web.oauth.SSOToken;

public class TokenUtil {
	@Resource
	private static SysElementDao sysElementDao;

	public static SSOToken getToken(){

		Subject subject = SecurityUtils.getSubject();
		SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
		if (ss != null){
			return (SSOToken) ss.getPrimaryPrincipal();
		}

		return null;
	}
	public static Map<String,Object> isPermitted(HttpServletRequest request){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("type", "success");
		Subject subject = SecurityUtils.getSubject();
		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		String requestUrl = WebUtils.getPathWithinApplication(httpServletRequest); /**相对路径**/
		if(requestUrl!=null&&!"".equals(requestUrl)
        		&&!StringUtils.isEmpty(getEleMentCode(requestUrl))&&!subject.isPermitted(getEleMentCode(requestUrl))){
			
			
			
        }
		return resultMap;
	}
	/**
	 * 根据访问路径获取资源编码
	 * @param requestUrl
	 * @return
	 */
	private static String getEleMentCode(String requestUrl) {
		try {
			return sysElementDao.getCodeByUrl(TokenUtil.getToken().getObjectContext(), requestUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
