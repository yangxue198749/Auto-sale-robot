package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtPartnerService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping("partner")
public class MtPartnerController {
	
	@Resource
	private IMtPartnerService iMtPartnerService;
	
	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@RequestMapping("home")
	@ResponseBody
	public ModelAndView home(){
		return new ModelAndView("mtPartner/partner_list" ,"partnerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.PARTNER_TYPE, TokenUtil.getToken().getObjectContext()));//合伙人类型
	}
	
	@RequestMapping("createPage")
	@ResponseBody
	public ModelAndView createPage(){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtPartner/dialog/add_partner_dialog");
		modelAndView.addObject("partnerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.PARTNER_TYPE, objectContext));//合伙人类型
		return modelAndView;
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Integer createPartner(TMtPartner tMtPartner){
		return iMtPartnerService.addPartner(tMtPartner,  TokenUtil.getToken().getSysUser() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updatePage(Integer ids){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtPartner/dialog/edit_partner_dialog" ,"partner" ,iMtPartnerService.getTMtCustomerById(ids, objectContext));
		modelAndView.addObject("partnerType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.PARTNER_TYPE, objectContext));//合伙人类型
		return modelAndView;
	}
	
	@RequestMapping("update")
	@ResponseBody
	public Integer updatePartner(TMtPartner tMtPartner){
		return iMtPartnerService.updatePartner(tMtPartner, TokenUtil.getToken().getSysUser().getId() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Boolean deletePartner(String ids){
		return iMtPartnerService.delPartner(ids.split(","), TokenUtil.getToken().getObjectContext());
	}
	
	
	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView list(Integer searchType ,String searchName ,String partnerType ,Integer pageSize ,Integer pageNo){
		
		if(searchType == null) searchType =0;
		String partnerName = null;
		String manageName = null;
		switch (searchType) {
		case 0:
			break;
		case 1:
			partnerName = searchName;
			break;
		case 2:
			manageName = searchName;
			break;
		}
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtPartner/page/partner_page",iMtPartnerService.list(partnerName, manageName, partnerType, pageSize, pageNo, user,context));
		return modelAndView;
	}

}
