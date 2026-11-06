package com.kdx.vms.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.service.IStVmTextService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping("stVmText")
public class StVmTextController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private IStVmTextService stVmTextService;
	@Resource
	private IStVmStrategyService stVmStrategyService;
	@RequestMapping("list")
	public String list(){
		
		
		return "StVmText/st_vm_text_list";
	}
	
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchStVmName,String searchStVmText){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/stVmText/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = stVmTextService.findStVmTextPage(token.getObjectContext(), token.getSysUser(), searchStVmName, searchStVmText, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("stVmStrategyList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "StVmText/page/st_vm_text_page";
	}
	
	@RequestMapping("addStVmTextDialog")
	public String addStVmTextDialog(){
		
		return "StVmText/dialog/add_stvmtext_dialog";
	}
	@RequestMapping("submitAddStVmText")
	@ResponseBody
	public Map<String,Object> submitAddStVmText(String addStVmName,String addStVmDesc,String addStVmText){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("title", "新建走马灯策略");
		SSOToken token = TokenUtil.getToken();
		try {
			String errMsg = stVmTextService.addStVmText(token.getObjectContext(), addStVmName, addStVmDesc, addStVmText, token.getSysUser());
			resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"error");
			resultMap.put("message", StringUtil.isEmpty(errMsg)?"新建走马灯策略成功":"新建走马灯策略失败");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("type", "error");
			resultMap.put("message", "系统内部异常,新建走马灯策略失败");
			logger.error(e.getLocalizedMessage());
		}
		return resultMap;
	}
	@RequestMapping("editStVmTextDialog")
	public String addStVmTextDialog(HttpServletRequest request,String ids){
		try {
			SSOToken token = TokenUtil.getToken();
			TStVmStrategy stVmStrategy = stVmStrategyService.findByPk(token.getObjectContext(), Integer.parseInt(ids));
			request.setAttribute("stVmStrategy", stVmStrategy);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		request.setAttribute("editStrategyId", ids);
		return "StVmText/dialog/edit_stvmtext_dialog";
	}

	@RequestMapping("submitEditStVmText")
	@ResponseBody
	public Map<String,Object> submitEditStVmText(String editStVmName,String editStVmDesc,String editStVmText,Integer editStrategyId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("title", "新建走马灯策略");
		SSOToken token = TokenUtil.getToken();
		try {
			String errMsg = stVmTextService.editStVmText(token.getObjectContext(), editStVmName, editStVmDesc, editStVmText, token.getSysUser(), editStrategyId);
			resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"error");
			resultMap.put("message", StringUtil.isEmpty(errMsg)?"修改走马灯策略成功":"修改走马灯策略失败");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("type", "error");
			resultMap.put("message", "系统内部异常,修改走马灯策略失败");
			logger.error(e.getLocalizedMessage());
		}
		return resultMap;
	}
	@RequestMapping("submitDeleteStVmText")
	@ResponseBody
	public Map<String,Object> submitDeleteStVmText(Integer[] ids){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("title", "删除走马灯策略");
		SSOToken token = TokenUtil.getToken();
		try {
			String errMsg = stVmTextService.deleteByStrategyIds(token.getObjectContext(),token.getSysUser(),Arrays.asList(ids));
			resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"error");
			resultMap.put("message", StringUtil.isEmpty(errMsg)?"删除走马灯策略成功":"删除走马灯策略失败");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("type", "error");
			resultMap.put("message", "系统内部异常,删除走马灯策略失败");
			logger.error(e.getLocalizedMessage());
		}
		return resultMap;
	}
	@RequestMapping("showText")
	public String showText(HttpServletRequest request,Integer strategyId){
		try {
			SSOToken token = TokenUtil.getToken();
			TStVmText stVmText = stVmTextService.findByStrategyId(token.getObjectContext(), strategyId);
			request.setAttribute("stVmText", stVmText);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "StVmText/dialog/show_stvmtext_dialog";
	}
}
