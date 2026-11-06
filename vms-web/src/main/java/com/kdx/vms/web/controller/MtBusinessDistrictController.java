package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.service.IMtBusinessDistrictService;
import com.kdx.vms.web.tags.TokenUtil;

/**
 * 场景信息
 * @date 2016年12月5日 上午10:36:24 
 * @author chensifan
 * @version 1.0
 */
@Controller
@RequestMapping(value = "businessDistrict")
public class MtBusinessDistrictController {
	
	@Resource
	private IMtBusinessDistrictService iMtBusinessDistrictService;
	
	@RequestMapping("home")
	public String home(){
		return "mtbusinessDistrict/businessDistrict_list";
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Integer createBusinessDistrict(String name){
		return iMtBusinessDistrictService.createBusinessDistrict(name, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("createPage")
	public String createPage(String name){
		return "mtbusinessDistrict/dialog/add_businessDistrict";
	}
	
	@RequestMapping("update")
	@ResponseBody
	public Integer updateBusinessDistrict(String name,String ids){
		return iMtBusinessDistrictService.updateBusinessDistrict(name, ids, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updatePage(String ids){
		return new ModelAndView("mtbusinessDistrict/dialog/update_businessDistrict","ids",ids);
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Boolean deleteBusinessDistrict(String ids){
		return iMtBusinessDistrictService.deleteBusinessDistrict(ids.split(","), TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("list")
	@ResponseBody
	public ModelAndView list(Integer pageSize ,Integer pageNo){
		return new ModelAndView("mtbusinessDistrict/page/businessDistrict_page", iMtBusinessDistrictService.list(pageSize, pageNo, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext()));
	}

}
