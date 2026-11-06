package com.kdx.vms.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtCustomerService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "customerAccount")
public class MtCustomerAccountController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";
	private final static String authToken = "430765a9bfb6177ea8627bd9c280a975";
	private final static String appId = "2c046def4a4443d795ac8f96094798e8";
	private final static String templateId = "33447";
	
	@Resource
	private IMtCustomerService iMtCustomerService;
	
	@Resource
	private ISysTypeInfoService iSysTypeInfoService;

	@RequestMapping(value = "home")
	public String home(){
		return "mtCustomerPay/mtCustomerPay_list";
	}
	
	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView list(Integer pageSize ,Integer pageNo){
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomerPay/page/mtCustomerPay_page");
		if(user.getCompany().getCompanyLevel() == TMtCompany.LEVEL_ONE){
			Map<String, Object> map = iMtCustomerService.list(null, null, null, null, pageSize, pageNo,user,context);
			map.put("url", "list");
			modelAndView.addAllObjects(map);
		}else{
			modelAndView.addObject("customer", user.getCompany().getCustomer());
		}
		modelAndView.addObject("giroAccountType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.GIRO_ACCOUNT_TYPE, context));//转账账号类型
		return modelAndView;
	}
	
	@RequestMapping(value = "updatePage")
	@ResponseBody
	public ModelAndView updateCustomerAccountPage(Integer ids){
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomerPay/dialog/update_mtCustomerPay_dialog","giroAccountType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.GIRO_ACCOUNT_TYPE, context));
		modelAndView.addObject("customer", iMtCustomerService.getTMtCustomerById(ids, context));//转账账号类型
		return modelAndView;
	}
	
	@RequestMapping(value = "update")
	@ResponseBody
	public Integer updateCustomerAccount(TMtCustomer tMtCustomer){
		return iMtCustomerService.updateCustomer(tMtCustomer, TokenUtil.getToken().getSysUser().getId(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("/passSMS")
	@ResponseBody
	public Boolean passSMS(HttpServletRequest request ,Integer customerId){
		try {
			String phone = iMtCustomerService.getTMtCustomerById(customerId, TokenUtil.getToken().getObjectContext()).getManagerPhone();
			Integer verificationCode = RandomUtils.nextInt(1000, 9999);
			request.getSession().setAttribute("customer_code"+customerId, verificationCode);
			request.getSession().setAttribute("customer_code_time"+customerId, new Date().getTime());
			UcPaasUtil.templateSMS(accountSid, authToken, appId, templateId, phone, verificationCode+"");//发送短信
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	@RequestMapping("/verificationCode")
	@ResponseBody
	public Map<String,Object> verificationCode(HttpServletRequest request ,String codeNumber ,Integer customerId){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("state", false);
		try {
			HttpSession session = request.getSession();
			Integer code = (Integer) session.getAttribute("customer_code"+customerId);
			Long time = (Long) session.getAttribute("customer_code_time"+customerId);
			if(code == null || StringUtils.isEmpty(codeNumber) || !codeNumber.equals(String.valueOf(code))){
				map.put("message", "验证码不对");
				return map;
			}else if((new Date().getTime() - time)/1000/60 > 10){
				map.put("message", "验证码超时");
				return map;
			}
			map.put("state", true);
			session.removeAttribute("customer_code"+customerId);
			session.removeAttribute("customer_code_time"+customerId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			map.put("message", "程序异常");
		}
		return map;
	}
	
}
