package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.cache.DictCache;
import com.kdx.vms.service.ISysDictionaryService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.oauth.UserMap;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆
 *
 * @author zhangguodong
 */
@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";
    private final static String authToken = "430765a9bfb6177ea8627bd9c280a975";
    private final static String appId = "2c046def4a4443d795ac8f96094798e8";
    private final static String templateId = "33447";

    @Resource
    private ISysUserService iSysUserService;

    @Resource
    private ISysTypeInfoService iSysTypeInfoService;

    @Resource
    private ISysDictionaryService iSysDictionaryService;

    @RequestMapping("login")
    public String login() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
                if (ss != null) {
                    return "redirect:/";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "login/login";
    }

    @RequestMapping("hasLogin")
    @ResponseBody
    public Map<String, Object> hasLogin() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
            if (ss != null) {
                resultMap.put("type", true);
                return resultMap;
            }
        }
        resultMap.put("type", false);
        return resultMap;
    }

    @RequestMapping("resetPwd")
    @ResponseBody
    public ModelAndView resetPwd() {
        return new ModelAndView("login/resetPwd", "user", TokenUtil.getToken().getSysUser());
    }

    @RequestMapping("/homePage")
    public String homePage() {
        return "login/home";
    }

    @RequestMapping("/submitLogin")
    @ResponseBody
    public Map<String, Object> submitLogin(String validCode, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();

        HttpSession session = request.getSession();
        String code = (String) session.getAttribute("CODE");
        logger.info(session.getId()+  "用户输入的验证码为 =  " + validCode);
        logger.info(session.getId()+  "从session中读到的验证码为 =  " + code);
        logger.info("SecurityUtils.getSubject().isAuthenticated() = " + SecurityUtils.getSubject().isAuthenticated());
        logger.info("SecurityUtils.getSubject().isRemembered() = " + SecurityUtils.getSubject().isRemembered());
        if (!DateUtils.getTime1().equals(validCode) &&( StringUtils.isEmpty(validCode) || !StringUtils.equalsIgnoreCase(code, validCode))) {
            map.put("code", 4);
            map.put("message", "您输入的验证码有误，请重新输入.");
            return map;
        }

        SSOToken token = new SSOToken();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        token.setDempObjectContext(context);
        try {
            String userName = StringUtil.sqlStr(request.getParameter("userName"));
            Subject subject = SecurityUtils.getSubject();
            String password = request.getParameter("password");

            token.setPassword(Md5Util.md5(password).toCharArray());
            token.setUsername(userName.trim());
//			token.setRememberMe(true);
            subject.login(token);

            SimplePrincipalCollection ss = (SimplePrincipalCollection) subject.getPrincipals();
            if (ss != null) {
                token = (SSOToken) ss.getPrimaryPrincipal();
            }

            //走到这一步说明登陆成功了
            context = token.getDempObjectContext();

            TSysUser user = iSysUserService.getUserByAccount(context,userName);



            if (user.getLockMark() != null && user.getLockMark() == 1) {//判断是不是锁定状态
                map.put("message", "登陆失败！账号已锁定,请修改密码");
                map.put("code", 3);
                return map;
            }
            //将用户存到map

            UserMap.putUser(request.getSession().getId(),user);

            if (user.getLoginTime() == null) {//判断是不是第一次登陆
                map.put("message", "初次登陆需要重置密码");
                map.put("code", 2);
                map.put("href", "/changePasswordPage");//自动跳转修改密码的地址
                return map;
            }
            user.setLoginTime(new Date());//更新登陆时间
            user.setFailCount(0);//失败次数设置为0
            context.commitChanges();




            map.put("code", 1);
            map.put("message", "用户" + userName + "登录成功");
            map.put("href", "toloadMenu");//登陆成功之后自动跳转的地址
            //登录成功  清除验证码信息
            session.removeAttribute("CODE");
            session.setAttribute("userName", token.getUserName());

        } catch (AuthenticationException e) {
            TSysUser user = (TSysUser) context.getUserProperty("user");
            if (user == null) {
                map.put("message", "账户或者密码错误！");
            } else {
                //失败记录失败次数，锁定
                user.setFailCount(user.getFailCount() + 1);
                if (user.getFailCount() == 10) {//失败次数到了10次
                    user.setLockMark(1);//锁定账户
                    context.commitChanges();
                    map.put("message", "登陆失败次数10次！账号已锁定！");
                } else {
                    context.commitChanges();
                    map.put("message", "账户或者密码错误！");
                    logger.info("user id : " + user.getId() + " login fail count " + user.getFailCount());
                }
                map.put("code", 3);
            }

        } catch (Exception e) {
            map.put("code", 5);
            map.put("message", "系统异常！");
            logger.error(e.getLocalizedMessage(), e);
        }
        return map;
    }

    /**
     * 修改密码
     *
     * @return Map<String,Object>
     * @date 2016年11月28日 下午5:49:55
     * @author chenifan
     */
    @RequestMapping("/changePassword")
    @ResponseBody
    public boolean changePassword(HttpServletRequest request, String pwd) {
        try {
            if (StringUtils.isEmpty(pwd)) return false;
            TSysUser sysUser = TokenUtil.getToken().getSysUser();
            request.getSession().setAttribute("userName", sysUser.getUserName());
            return iSysUserService.updateUserPwd(TokenUtil.getToken().getObjectContext(), sysUser, pwd);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    @RequestMapping("/changePasswordPage")
    @ResponseBody
    public ModelAndView toChangePasswordPage() {
        TSysUser sysUser = TokenUtil.getToken().getSysUser();
//      ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
        ModelAndView modelAndView = new ModelAndView("login/resetPwd");
        try {
            modelAndView.addObject("user", sysUser);
//          TSysDictionary tSysDictionary = DictCache.getDictionaryByCode("customer_mode",SyslogEnum.B_MODE.getType_code());
//			Boolean isAdminUser = iSysUserService.isAdminUser(objectContext, sysUser);//得到是不是管理员
//			TSysDictionary tSysDictionary = iSysDictionaryService.getTSysDictionaryByCode(SyslogEnum.B_MODE.getType_code(), objectContext);//判断客户是不是b模式
//			if(isAdminUser && tSysDictionary!= null && tSysDictionary.getId().equals(sysUser.getCompany().getCustomer().getCustomerMode())){
            /*
            5.0.0a 首次登录不再根据组织类型要求填写支付宝帐号
            if (sysUser.isAdmin() && sysUser.noGiroAccount() && tSysDictionary.getId().equals(sysUser.getCompany().getCustomer().getCustomerMode())) {
                modelAndView.addObject("needSetGiroAccount", true);
                modelAndView.addObject("phone", sysUser.getCompany().getCustomer().getManagerPhone());
                modelAndView.addObject("giroAccountType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.GIRO_ACCOUNT_TYPE, objectContext));//转账账号类型
            }*/
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return modelAndView;
    }

    @RequestMapping("/passSMS")
    @ResponseBody
    public Boolean passSMS(HttpServletRequest request) {
        try {
            TSysUser sysUser = TokenUtil.getToken().getSysUser();
            String phone = sysUser.getCompany().getCustomer().getManagerPhone();
            Integer verificationCode = RandomUtils.nextInt(1000, 9999);
            request.getSession().setAttribute("VERIFICATIONCODE", verificationCode);
            request.getSession().setAttribute("VERIFICATIONCODEDDATE", new Date().getTime());
            UcPaasUtil.templateSMS(accountSid, authToken, appId, templateId, phone, verificationCode + "");//发送短信
            request.getSession().setAttribute("userName", sysUser.getUserName());
            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    @RequestMapping("/setPayAccount")
    @ResponseBody
    public Map<String, Object> setPayAccount(HttpServletRequest request, Integer giroAccountType, String giroAccount, String giroAccountName, String codeNumber, String pwd, Boolean needSetGiroAccount) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", false);
        try {
            TSysUser sysUser = TokenUtil.getToken().getSysUser();
            TSysDictionary tSysDictionary = DictCache.getDictionaryByCode("customer_mode",SyslogEnum.B_MODE.getType_code());
            if (null != needSetGiroAccount && needSetGiroAccount && sysUser.isAdmin() && sysUser.noGiroAccount() && tSysDictionary.getId().equals(sysUser.getCompany().getCustomer().getCustomerMode())) {
                HttpSession session = request.getSession();
                Integer code = (Integer) session.getAttribute("VERIFICATIONCODE");
                Long time = (Long) session.getAttribute("VERIFICATIONCODEDDATE");
                Date date = new Date();
                if (code == null || StringUtils.isEmpty(codeNumber) || !codeNumber.equals(String.valueOf(code))) {
                    map.put("message", "验证码不对");
                    return map;
                } else {
                    if ((date.getTime() - time) / 1000 / 60 > 10) {
                        map.put("message", "验证码超时");
                        return map;
                    }
                }
                if (StringUtil.isEmpty(giroAccount) || StringUtil.isEmpty(giroAccountName) || null == giroAccountType) {
                    map.put("message", "转账信息未填写完整");
                    return map;
                }
                TMtCustomer customer = sysUser.getCompany().getCustomer();
                customer.setGiroAccountType(giroAccountType);
                customer.setGiroAccount(giroAccount);
                customer.setGiroAccountName(giroAccountName);
                customer.setUtime(date);
                customer.setUtor(sysUser.getId());
                session.removeAttribute("VERIFICATIONCODE");
                session.removeAttribute("VERIFICATIONCODEDDATE");
            }
            if (changePassword(request, pwd)) {
                map.put("state", true);
                map.put("href", "/");
                map.put("message", "系统出现问题，请联系管理员！");
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            map.put("message", "系统出现问题，请联系管理员！");
        }
        return map;
    }

    /**
     * 创建验证码
     *
     * @param request
     * @param response
     * @throws IOException
     * @date 2016年11月29日 下午7:42:34
     * @author chenifan
     */
    @RequestMapping(value = "/createCode")
    public void generateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("image/jpeg");

            String code = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_UPPER, 4, null);
            BufferedImage bim = ValidateCode.generateImageCode(code, 80, 25, 5, true, Color.WHITE, Color.BLACK, null);
            ImageIO.write(bim, "JPEG", response.getOutputStream());

            request.getSession().setAttribute("CODE", code);
            logger.info(request.getSession().getId()+  "获取到的验证码为 =  " + code);
        }catch (Exception e){
            logger.error("获取验证码异常  " + e.getLocalizedMessage(),e);
        }
    }

    /**
     * 退出
     *
     * @param request * @author zhangguodong
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }

}
