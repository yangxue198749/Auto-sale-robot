package com.kdx.mp.web.controller;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserBind;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by yangshuxi on 2016/12/28.
 */
@RequestMapping("mp/home")
@Controller
public class MpHomeController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("")
    public String home(HttpServletRequest request) throws IOException {
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
        try {
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        if(null == currentUser){
            return null;
        }
        String account = currentUser.getUserAccount();
        String url     = currentUser.getMpHeadImgUrl();
        request.setAttribute("account", account);
        request.setAttribute("headImg", url);
    } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
    }
        return "mp/home/home";
    }

    @RequestMapping("unbundle")
    @ResponseBody
    public Map<String, String> unbundle(HttpServletRequest request) {
        LOGGER.info("unbundle");
        Map<String, String> resMap = new HashMap<>();
        resMap.put("type", "error");
        ObjectContext context = null;
        try {
            // 获取当前用户
//            TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            // 获取当前openId
            String   openId      = (String)   request.getSession().getAttribute("openId");
            LOGGER.info("currUser:" + currentUser + " openId:" + openId);
            if(null == currentUser || StringUtil.isEmpty(openId)) {
                LOGGER.error("currentUser or openId is null.");
                return resMap;
            }
            context = currentUser.getObjectContext();
            // 获取用户绑定关系，当前只会有一个
            List<TSysUserBind> bindList = currentUser.getBind();
            for (int i = 0; i < bindList.size(); i++) {
                TSysUserBind bind = bindList.get(i);
                if (null != bind) {
                    // 匹配当前openId，删除绑定数据
                    if (openId.equals(bind.getOuterId())) {
                        context.deleteObject(bind);
                        break;
                    }
                }
            }
            // 提交操作
            context.commitChanges();
            // 删除session用户对象
//            request.getSession().setAttribute("mpUser", null);
            UserMap.removeUser(SecurityUtils.getSubject().getSession().getId());
            // 跳转到登陆(绑定)页面
            resMap.put("type", "success");
            resMap.put("url", "/mp/auth/login");
        } catch (Exception e) {
            if (null != context) context.rollbackChanges();
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

    @RequestMapping("test")
    public String test(HttpServletRequest request) {
        return "mp/test/test";
    }

}
