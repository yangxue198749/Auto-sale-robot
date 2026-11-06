package com.kdx.mp.web.filter;

import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.cache.ElementCache;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by yangshuxi on 2016/12/20.
 */
public class mpSessionFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址
     **/
    private String unauthorizedUrl = "/mp/auth/unauthorized";
    /**
     * 获取JS-SDK配置地址
     **/
    private String jsSdkConfigUrl = "/mp/activate/config";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;
        String url = req.getRequestURI();
        String uri = url.replaceFirst(req.getContextPath(), "");
        Enumeration paramNames = request.getParameterNames();
        StringBuilder builder = new StringBuilder();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (builder.length() > 0) builder.append("&");
            builder.append(paramName).append("=").append(paramValue);
        }
        //获得URI
        uri = uri.replaceFirst("//", "/");
        if (uri.endsWith("bind") || uri.endsWith("sso") || uri.endsWith("login") || uri.endsWith("callback")) {
            chain.doFilter(request, response);
        } else {
 //           UserMap.putUser(SecurityUtils.getSubject().getSession().getId(), Cayenne.objectForPK(ObjectContextFactory.getInstance().newObjectContext(),TSysUser.class,5121));
            boolean isLogin = this.checkLogin(req);
            if (isLogin) {
//                TSysUser mpUser = (TSysUser) req.getSession().getAttribute("mpUser");
                TSysUser mpUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
                boolean hasPermission = this.hasPermission(req, uri, mpUser);
                logger.info("用户" + mpUser.getUserAccount() + "访问魔核地址" + uri + "，携带参数" + builder.toString());
                if (hasPermission || uri.contains(unauthorizedUrl) || uri.contains(jsSdkConfigUrl)) {
                    chain.doFilter(request, response);
                } else {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                }
            } else {
                String redirectUrl = req.getScheme() + "://" + req.getServerName() + req.getContextPath() + "/mp/auth/sso";
                String mpUrl = WeChatUtils.getAuthUrl(redirectUrl, WeChatUtils.BASE_SCOPE, url + "?" + builder.toString());
                WebUtils.issueRedirect(request, response, mpUrl);
            }
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * @param :rq-HTTP请求
     * @throws Exception
     * @Description:登录校验
     * @return：true-成功；false-失败
     */
    private boolean checkLogin(HttpServletRequest req) {
//        HttpSession session = req.getSession();
//        TSysUser user = (TSysUser) session.getAttribute("mpUser");
        TSysUser user = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        return user != null && user.getDeleteMark().equals(TSysUser.DELETE_MARK_EXIST);
    }

    private boolean hasPermission(HttpServletRequest req, String uri, TSysUser sysUser) {
        return !ElementCache.getElementSet().contains(uri)||sysUser.getAllPermission().contains(uri);
    }
}
