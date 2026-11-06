package com.kdx.vms.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "sysLog")
public class SysLogController {
	
	@Resource
	private ISysLogService iSysLogService;
	
    @RequestMapping("home")
	public String home(){
		return "sysLog/sysLog_list";
	}
    
    @RequestMapping("list")
    @ResponseBody
    public ModelAndView list(String userName , Integer pageSize ,Integer pageNo){
    	return new ModelAndView("sysLog/page/sysLog_page", iSysLogService.list(TokenUtil.getToken().getSysUser() ,userName, pageSize, pageNo, TokenUtil.getToken().getObjectContext()));
    }

}
