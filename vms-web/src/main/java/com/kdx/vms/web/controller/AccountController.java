package com.kdx.vms.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "account")
public class AccountController {

	private final static String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";
	private final static String authToken = "430765a9bfb6177ea8627bd9c280a975";
	private final static String appId = "2c046def4a4443d795ac8f96094798e8";
	private final static String templateId = "33447";

	@Resource
	private ISysUserService iSysUserService;

	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "home")
	@ResponseBody
	public ModelAndView home(HttpServletRequest request){
//		HttpSession session = request.getSession();
//		session.setAttribute("userName", TokenUtil.getToken().getSysUser().getUserName());
		return new ModelAndView("account/account_list" ,"user" ,TokenUtil.getToken().getSysUser());
	}

	@RequestMapping(value = "updateUserNamePage")
	public String updateUserNamePage(){
		return "account/dialog/changeUserName_dialog";
	}

	/**
	 * 更新用户名
	 */
	@RequestMapping(value = "updateUserName")
	@ResponseBody
	public Boolean updateUserName(HttpServletRequest request ,String userName){
		SSOToken token = TokenUtil.getToken();
		Boolean b = iSysUserService.updateUserName(token.getObjectContext(), token.getSysUser(), userName);
		if(b != null && b){
			HttpSession session = request.getSession();
			session.setAttribute("userName", TokenUtil.getToken().getSysUser().getUserName());
		}
		return b;
	}

	@RequestMapping(value = "updateUserPhonePage")
	public ModelAndView updateUserPhonePage(){
		return new ModelAndView("account/dialog/verifyChangePhone_dialog", "user" ,TokenUtil.getToken().getSysUser());
	}

	/**
	 * 更新手机号
	 */
	@RequestMapping(value = "updateUserPhone")
	@ResponseBody
	public Map<String, Object> updateUserPhone(HttpServletRequest request ,String newUserPhone ,String newUserPhoneCode){
		Map<String, Object> map = verifySMSCode(request, newUserPhoneCode ,"newUserPwdCode" ,"newUserPwdCodeTime");
		if(map.get("state").equals(true)){//验证码通过
			Boolean updateUserPhone = iSysUserService.updateUserPhone(TokenUtil.getToken().getObjectContext(), TokenUtil.getToken().getSysUser(), newUserPhone);
			if(!updateUserPhone){
				map.put("state", false);
				map.put("message", "更新失败！");
			}
		}
		return map;
	}

	@RequestMapping(value = "updateUserPasswordPage")
	@ResponseBody
	public ModelAndView updateUserPasswordPage(){
		return new ModelAndView("account/dialog/verifyChangePassword_dialog", "user" ,TokenUtil.getToken().getSysUser());
	}

	/**
	 * 更新密码
	 */
	@RequestMapping(value = "updateUserPassword")
	@ResponseBody
	public Boolean updateUserPassword(String pwd){
		return iSysUserService.updateUserPwd(TokenUtil.getToken().getObjectContext(), TokenUtil.getToken().getSysUser(), pwd);
	}

	/**
	 * 验证修改手机号发送的验证码
	 */
	@RequestMapping(value = "verifyUserPhoneCode")
	@ResponseBody
	public Map<String, Object> verifyUserPhoneCode(HttpServletRequest request ,String userPhoneCode){
		return verifySMSCode(request, userPhoneCode ,"userPhoneCode" ,"userPhoneCodeTime");
	}

	/**
	 * 验证修改密码发送的验证码
	 */
	@RequestMapping(value = "verifyUserPwdCode")
	@ResponseBody
	public Map<String, Object> verifyUserPwdCode(HttpServletRequest request ,String userPwdCode){
		return verifySMSCode(request, userPwdCode ,"userPwdCode" ,"userPwdCodeTime");
	}







	/**
	 * 修改手机号发送验证码
	 */
	@RequestMapping("/passUserPhoneCode")
	@ResponseBody
	public Boolean passUserPhoneCode(HttpServletRequest request){
		return postSMS(request ,"userPhoneCode" ,"userPhoneCodeTime" ,null);
	}

	/**
	 * 修改密码发送验证码
	 */
	@RequestMapping("/passUserPwdCode")
	@ResponseBody
	public Boolean passUserPwdCode(HttpServletRequest request){
		return postSMS(request ,"userPwdCode" ,"userPwdCodeTime" ,null);
	}

	/**
	 * 修改手机号页面新的手机号验证码
	 */
	@RequestMapping("/passNewUserPhoneCode")
	@ResponseBody
	public Boolean passNewUserPhoneCode(HttpServletRequest request , String newUserPhone){
		return postSMS(request ,"newUserPwdCode" ,"newUserPwdCodeTime" ,newUserPhone);
	}

	private Map<String, Object> verifySMSCode(HttpServletRequest request, String userPhoneCode ,String name ,String timeName) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("state", false);
		HttpSession session = request.getSession();
		Integer code = (Integer) session.getAttribute(name);
		Long time = (Long) session.getAttribute(timeName);
		if(code == null || StringUtils.isEmpty(userPhoneCode) || !userPhoneCode.equals(String.valueOf(code))){
			map.put("message", "验证码不对");
		}else if((new Date().getTime() - time)/1000/60 > 10){
			map.put("message", "验证码超时");
		}else{
			map.put("state", true);
			session.removeAttribute(name);
			session.removeAttribute(timeName);
		}
		return map;
	}

	private Boolean postSMS(HttpServletRequest request ,String name ,String timeName ,String phone) {
		try {
			TSysUser sysUser = TokenUtil.getToken().getSysUser();
			if(StringUtils.isEmpty(phone)){
				phone = sysUser.getUserPhone();
			}
			Integer verificationCode = RandomUtils.nextInt(1000, 9999);
			request.getSession().setAttribute(name, verificationCode);
			request.getSession().setAttribute(timeName, new Date().getTime());
			UcPaasUtil.templateSMS(accountSid, authToken, appId, templateId, phone, verificationCode+"");//发送短信
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}


}
