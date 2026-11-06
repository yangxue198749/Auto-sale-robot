package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.service.ISysDictionaryService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping("dictionary")
public class SysDictionaryController {
	
	@Resource
	private ISysDictionaryService iSysDictionaryService;

	@RequestMapping("list")
	@ResponseBody
	public ModelAndView list(Integer tSysTypeInfoId ,String name ,Integer pageSize ,Integer pageNo){
		return new ModelAndView("sysType/page/dictionary_page",iSysDictionaryService.list(tSysTypeInfoId, name, pageSize, pageNo, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("createPage")
	public String createPage(){
		return "sysType/dialog/add_dictionary_dialog";
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Integer createSysDictionary(Integer tSysTypeInfoId ,TSysDictionary tSysDictionary){
		return iSysDictionaryService.addSysTypeinfo(tSysTypeInfoId, tSysDictionary, TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updatePage(Integer ids){
		return new ModelAndView("sysType/dialog/edit_dictionary_dialog" ,"dictionary" ,iSysDictionaryService.getTSysDictionaryById(ids, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("update")
	@ResponseBody
	public Integer updateSysDictionary(TSysDictionary tSysDictionary){
		return iSysDictionaryService.updateSysTypeinfo(tSysDictionary, TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Boolean deleteSysDictionary(String ids){
		return iSysDictionaryService.deleteSysTypeinfo(ids.split(","), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("isNotExist")
	@ResponseBody
	public Boolean isNotExist(String code,Integer typeInfoId){
		if(code == null) return true;
		TSysDictionary tSysDictionary = iSysDictionaryService.getTSysDictionaryByCodeAndTypeInfoId(code, typeInfoId, TokenUtil.getToken().getObjectContext());
		if(tSysDictionary == null) return true;
		return false;
	}
	
	
}
