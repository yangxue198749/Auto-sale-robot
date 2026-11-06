package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtCustomerService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "customer")
public class MtCustomerController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@Resource
	private IMtCustomerService iMtCustomerService;
	
	@RequestMapping(value = "home")
	@ResponseBody
	public ModelAndView home(){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomer/customer_list");
		modelAndView.addObject("customerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_TYPE, objectContext));//客户类型
		modelAndView.addObject("customerMode", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_MODE, objectContext));//客户模式
		return modelAndView;
	}

	@RequestMapping(value = "create")
	@ResponseBody
	public Integer createCustomer(TMtCustomer tMtCustomer){
		return iMtCustomerService.addCustomer(tMtCustomer, TokenUtil.getToken().getSysUser().getId() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("createPage")
	@ResponseBody
	public ModelAndView createPage(){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomer/dialog/add_customer_dialog");
		modelAndView.addObject("customerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_TYPE, objectContext));//客户类型
		modelAndView.addObject("customerMode", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_MODE, objectContext));//客户模式
		return modelAndView;
	}
	
	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView list(Integer searchType ,String searchName ,String customerMode ,String customerType ,Integer pageSize ,Integer pageNo){
		
		if(searchType == null) searchType =0;
		String customerName = null;
		String manageName = null;
		switch (searchType) {
		case 0:
			break;
		case 1:
			customerName = searchName;
			break;
		case 2:
			manageName = searchName;
			break;
		}
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomer/page/customer_page",iMtCustomerService.list(customerName, manageName, customerMode, customerType, pageSize, pageNo,user,context));
		return modelAndView;
	}
	
	@RequestMapping(value = "update")
	@ResponseBody
	public Integer updateCustomer(TMtCustomer tMtCustomer){
		return iMtCustomerService.updateCustomer(tMtCustomer, TokenUtil.getToken().getSysUser().getId() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping(value = "updatePage")
	@ResponseBody
	public ModelAndView updatePage(Integer ids){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtCustomer/dialog/edit_customer_dialog","customer",iMtCustomerService.getTMtCustomerById(ids, objectContext));
		modelAndView.addObject("customerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_TYPE, objectContext));//客户类型
		modelAndView.addObject("customerMode", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CUSTOMER_MODE, objectContext));//客户模式
		return modelAndView;
	}
	
	@RequestMapping(value = "delete")
	@ResponseBody
	public Boolean deleteCustomer(String customerIds){
		return iMtCustomerService.delCustomer(customerIds.split(","), TokenUtil.getToken().getObjectContext());
	}
	
}
