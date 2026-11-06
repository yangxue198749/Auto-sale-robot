package com.kdx.vms.web.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.service.ISkuTypeService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "skuType")
public class SkuTypeController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ISkuTypeService iSkuTypeService;
	
	@RequestMapping(value = "home")
	public String home(){
		return "skuType/skuType_list";
	}

	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView listTypeGroup(Integer pid ,Integer pageSize ,Integer pageNo ){
		ModelAndView modelAndView = new ModelAndView("skuType/page/skuType_page",iSkuTypeService.list(pid ,pageSize, pageNo, TokenUtil.getToken().getObjectContext()));
		modelAndView.addObject("isOnelevel", pid == null ? true :false);
		return modelAndView;
	}
	
	@RequestMapping(value = "update")
	@ResponseBody
	public Integer updateSkuTypeGroup(Integer skuTypeId ,String skuTypeName){
		return iSkuTypeService.updateSkuType(skuTypeId ,skuTypeName ,TokenUtil.getToken().getSysUser().getId() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping(value = "create")
	@ResponseBody
	public Integer createSkuTypeGroup(Integer pid ,String skuTypeName){
		return iSkuTypeService.newSkuType(pid, skuTypeName, TokenUtil.getToken().getSysUser().getId() ,TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping(value = "delete")
	@ResponseBody
	public Boolean deleteSkuTypeGroup(String skuTypeIds){
		return iSkuTypeService.deleteSkuType(skuTypeIds.split(","), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping(value = "listType")
	@ResponseBody
	public ModelAndView listType(Integer pid ,Integer pageSize ,Integer pageNo ){
		return listTypeGroup(pid, pageSize, pageNo);
	}
	
	@RequestMapping(value = "updateType")
	@ResponseBody
	public Integer updateSkuType(Integer skuTypeId ,String skuTypeName){
		return updateSkuTypeGroup(skuTypeId, skuTypeName);
	}
	
	@RequestMapping(value = "createType")
	@ResponseBody
	public Integer createSkuType(Integer pid ,String skuTypeName){
		return createSkuTypeGroup(pid, skuTypeName);
	}
	
	@RequestMapping(value = "deleteType")
	@ResponseBody
	public Boolean deleteSkuType(String skuTypeIds){
		return deleteSkuTypeGroup(skuTypeIds);
	}
}
