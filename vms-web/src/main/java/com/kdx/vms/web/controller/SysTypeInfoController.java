package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping("sysType")
public class SysTypeInfoController {

	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@RequestMapping("home")
	public String home(){
		return "sysType/sysType_list";
	}
	
	@RequestMapping("list")
	@ResponseBody
	public ModelAndView list(String name ,Integer pageSize ,Integer pageNo){
		return new ModelAndView("sysType/page/sysType_page",iSysTypeInfoService.list(name, pageSize, pageNo, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("createPage")
	public String createPage(){
		return "sysType/dialog/add_sysType_dialog";
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Integer createSysTypeInfo(TSysTypeInfo tSysTypeInfo){
		return iSysTypeInfoService.addSysTypeinfo(tSysTypeInfo, TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updatePage(Integer ids){
		return new ModelAndView("sysType/dialog/edit_sysType_dialog","sysType" ,iSysTypeInfoService.getTSysTypeInfoById(ids, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("update")
	@ResponseBody
	public Integer updateSysTypeInfo(TSysTypeInfo tSysTypeInfo){
		return iSysTypeInfoService.updateSysTypeinfo(tSysTypeInfo, TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Boolean deleteSysTypeInfo(String ids){
		return iSysTypeInfoService.deleteSysTypeinfo(ids.split(","), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("isNotExist")
	@ResponseBody
	public Boolean isNotExist(String code){
		if(code == null) return true;
		TSysTypeInfo tSysTypeInfo = iSysTypeInfoService.findSysTypeinfo(code, TokenUtil.getToken().getObjectContext());
		if(tSysTypeInfo == null) return true;
		return false;
	}
	
	
}
