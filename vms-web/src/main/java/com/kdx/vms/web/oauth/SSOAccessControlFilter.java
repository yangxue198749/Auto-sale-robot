package com.kdx.vms.web.oauth;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.orm.dao.SysElementDao;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源拦截器
 * @author zhangguodong
 *
 */
public class SSOAccessControlFilter extends AccessControlFilter{

	/**没有权限地址**/
	private String unauthorizedUrl = "/unauthorized"; 
	
	@Resource
	private SysElementDao sysElementDao;
	public SSOAccessControlFilter() {
		
	}
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		/**判断用户是否已经到期**/
		Subject subject = SecurityUtils.getSubject();
		SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
		if (ss == null) return false;
		
		
		/**如果访问地址在授权资源中，并且已授权则不进行拦截**/
		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		String requestUrl = WebUtils.getPathWithinApplication(httpServletRequest); /**相对路径**/

		/**如果访问地址在授权资源中，并且已授权则不进行拦截**/
        if(requestUrl!=null&&!"".equals(requestUrl)
        		&&!StringUtils.isEmpty(getEleMentCode(requestUrl))&&!subject.isPermitted(getEleMentCode(requestUrl))){
    			return false;
        }
        return true;
	}
	
	

	/**
	 * 根据访问路径获取资源编码
	 * @param requestUrl
	 * @return
	 */
	private String getEleMentCode(String requestUrl) {
		try {
			return sysElementDao.getCodeByUrl(TokenUtil.getToken().getObjectContext(), requestUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//		 WebUtils.issueRedirect(request, response, "/errors/error.jsp");  
		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		String requestMethod = httpServletRequest.getMethod();
		String isModal = httpServletRequest.getParameter("isModal");
		if("POST".equals(requestMethod)&&StringUtils.isEmpty(isModal)){
//			 WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			 Map<String,Object> resultMap = new HashMap<String, Object>();
			 resultMap.put("type", "error");
			 resultMap.put("message", "抱歉,您恐怕没有权限这样做");
			 response.getWriter().write(JSON.toJSONString(resultMap));
		}else{
			WebUtils.issueRedirect(request, response, unauthorizedUrl ,null,false);
		}
		return false;
	}
}
