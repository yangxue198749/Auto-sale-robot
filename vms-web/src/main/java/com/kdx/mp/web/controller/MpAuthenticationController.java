package com.kdx.mp.web.controller;

import com.kdx.mp.util.WeChatUtils;
import com.kdx.mp.service.IMpAuthenticationService;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserBind;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/20.
 */
@RequestMapping("mp/auth")
@Controller
public class MpAuthenticationController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMpAuthenticationService mpAuthenticationService;
    @Autowired
    private ISysUserService sysUserService;

    @RequestMapping("sso")
    public String mpSso(String code, String state, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            if (StringUtil.isEmpty(code)) return null;
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            response.setContentType("text/html;charset=UTF-8");
            String openId = (String) request.getSession().getAttribute("openId");
            if (StringUtil.isEmpty(openId)) {
                openId = WeChatUtils.getOpenId(code);
            }
            if (null != openId && !"".equals(openId)) {
                TSysUser mpUser = mpAuthenticationService.getMpUser(context, openId);
                request.getSession().setAttribute("openId", openId);
                if (null != mpUser) {
                    sysUserService.flashRoleCache(mpUser);
                    mpUser.setMpHeadImgUrl(WeChatUtils.getHeadImgUrl(openId));
//                request.getSession().setAttribute("mpUser", mpUser);
                    UserMap.putUser(SecurityUtils.getSubject().getSession().getId(), mpUser);
                    WebUtils.issueRedirect(request, response, state);
                    return null;
                }
            }
            WebUtils.issueRedirect(request, response, request.getContextPath() + "/" + "mp/auth/login");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("login")
    public String login(HttpServletResponse response, HttpServletRequest request) {
        try {
            String openId = (String) request.getSession().getAttribute("openId");
            if (StringUtil.isEmpty(openId)) {
                //如果session里没有openId不能访问登录页，先去获取openId
                String redirectUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath() + "/mp/auth/sso";
                String mpUrl = WeChatUtils.getAuthUrl(redirectUrl, WeChatUtils.BASE_SCOPE, "/mp/home");
                try {
                    WebUtils.issueRedirect(request, response, mpUrl);
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/auth/bind";
    }

    @RequestMapping("bind")
    public
    @ResponseBody
    Map<String, Object> bind(HttpServletResponse response, HttpServletRequest request, String username, String password) {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> map = new HashMap<>();
        try {
            String type = "";
            String message = "";
            String openId = (String) request.getSession().getAttribute("openId");
            if (StringUtil.isEmpty(openId) || StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
                type = "error";
                message = "账号或密码错误";
                map.put("type", type);
                map.put("message", message);
                return map;
            }
            //判断openId是否被绑定过，如果已绑定直接返回成功
            TSysUser mpUser = mpAuthenticationService.getMpUser(context, openId);
            if (null != mpUser) {
                sysUserService.flashRoleCache(mpUser);
                mpUser.setMpHeadImgUrl(WeChatUtils.getHeadImgUrl(openId));
//            request.getSession().setAttribute("mpUser", mpUser);
                UserMap.putUser(SecurityUtils.getSubject().getSession().getId(), mpUser);
//            request.getSession().setAttribute("objectContext",ObjectContextFactory.getInstance().newObjectContext());
                type = "success";
                message = "成功";
                map.put("type", type);
                map.put("message", message);
                return map;
            }
            mpUser = mpAuthenticationService.getMpUser(context, username, password);
            if (null == mpUser) {
                //如果获取不到说明账号密码错误
                type = "error";
                message = "账号或密码错误";
                map.put("type", type);
                map.put("message", message);
                return map;
            }
            List<TSysUserBind> list = mpUser.getBind();
            if (list != null && !list.isEmpty()) {
                //判断用户是否被绑定过，如果被绑定过不能再次绑定，返回错误
                for (TSysUserBind sysUserBind : list) {
                    if (sysUserBind.getBindType().equals(TSysUserBind.BIND_TYPE_WECHAT)) {
                        type = "error";
                        message = "该账号已绑定过其他微信账号，无法重复绑定";
                        map.put("type", type);
                        map.put("message", message);
                        return map;
                    }
                }
            }
            mpAuthenticationService.bindMpUser(context, openId, mpUser);
            sysUserService.flashRoleCache(mpUser);
            mpUser.setMpHeadImgUrl(WeChatUtils.getHeadImgUrl(openId));
//        request.getSession().setAttribute("mpUser", mpUser);
            UserMap.putUser(SecurityUtils.getSubject().getSession().getId(), mpUser);
//        request.getSession().setAttribute("objectContext",ObjectContextFactory.getInstance().newObjectContext());
            type = "success";
            message = "绑定成功";
            map.put("type", type);
            map.put("message", message);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return map;
    }

    @RequestMapping("unauthorized")
    public String unauthorized() {
        return "mp/auth/unauthorized";
    }
}
