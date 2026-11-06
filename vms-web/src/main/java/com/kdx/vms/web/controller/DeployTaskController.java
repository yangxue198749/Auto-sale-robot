package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("deployTask")
@Controller
public class DeployTaskController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private ITaskVmService taskVmService;
	@Resource
	private ISysUserService sysUserService;
	@RequestMapping("list")
	public String list(){
		
		return "deployTask/deploy_task_list";
	}
	
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer searchField,String searchFieldValue,Integer searchTaskVmStatus,String startTime,String endTime){
		
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/deployTask/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.findPage(token.getObjectContext(), token.getSysUser(), searchField, searchFieldValue, searchTaskVmStatus, startTime, endTime, TTaskVm.TYPE_DEPLOY, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("taskVmList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "deployTask/page/deploy_task_page";
	}
	
	@RequestMapping("addTaskDialog")
	public String addTaskDialog(){
		
		return "deployTask/dialog/add_task_dialog";
	}
	@RequestMapping("addTask")
	@ResponseBody
	public Map<String,Object> addTask(Integer vmId,String ownerId,Integer defaultUserId,String taskTime,Integer addNodeId){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			Integer own = null;
			try {
				own = Integer.parseInt(ownerId);
			} catch (Exception e) {
				resultMap.put("type", "error");
				resultMap.put("message", "请选择负责人");
				return resultMap;
			}
			String errMsg = taskVmService.createdeployTask(token.getObjectContext(),token.getSysUser(),vmId,own,taskTime,addNodeId);
			if(StringUtil.isEmpty(errMsg)){
				resultMap.put("type", "success");
				resultMap.put("message", "新建成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", errMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("cancelTask")
	@ResponseBody
	public Map<String,Object> cancelTask(Integer[] ids){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			String errMsg = taskVmService.cancelTask(token.getObjectContext(),token.getSysUser(),ids);
			if(StringUtil.isEmpty(errMsg)){
				resultMap.put("type", "success");
				resultMap.put("message", "取消成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", errMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("mtVmList")
	public String mtVmList(){
		
		return "deployTask/dialog/select_mt_vm_dialog";
	}
	@RequestMapping("editTaskDialog")
	public String editTaskDialog(HttpServletRequest request,Integer ids){
		try {
			SSOToken token = TokenUtil.getToken();
			TTaskVm taskVm = taskVmService.findByPk(token.getObjectContext(),ids);
			if(taskVm != null){
				String html = sysUserService.findSysUsersByCompany(token.getSysUser().getCompany());
				request.setAttribute("taskVm", taskVm);
				request.setAttribute("html", html);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "deployTask/dialog/edit_task_vm_dialog";
	}
	@RequestMapping("mtVmPage")
	public String mtVmPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchNodeName,String searchInnerCode){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/deployTask/mtVmPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.mtVmPage(token.getObjectContext(),token.getSysUser(),searchNodeName,searchInnerCode,pageSize,TMtVm.STATUS_UNDEPLOY);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "deployTask/page/mt_vm_page";
	}
	@RequestMapping("editTaskVm")
	@ResponseBody
	public Map<String,Object> editTaskVm(Integer taskVmId,String editOwnerId,String taskTime){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			Integer own = null;
			try {
				own = Integer.parseInt(editOwnerId);
			} catch (Exception e) {
				resultMap.put("type", "error");
				resultMap.put("message", "请选择负责人");
				return resultMap;
			}
			String errMsg = taskVmService.editTaskVm(token.getObjectContext(),token.getSysUser(),taskVmId,own,null,taskTime);
			if(StringUtil.isEmpty(errMsg)){
				resultMap.put("type", "success");
				resultMap.put("message", "修改成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", errMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("publishTaskDialog")
	public String publishTaskDialog(HttpServletRequest request,Integer[] ids){
		try {
			SSOToken token = TokenUtil.getToken();
			String html = sysUserService.findSysUsersByCompany(token.getSysUser().getCompany());
			request.setAttribute("html", html);
			request.setAttribute("taskVmIdList", Arrays.asList(ids));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "deployTask/dialog/publish_task_dialog";
	}
	
	@RequestMapping("publishTask")
	@ResponseBody
	public Map<String,Object> publishTask(Integer[] ids,String ownerId,String taskTime){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			Integer own = null;
			try {
				own = Integer.parseInt(ownerId);
			} catch (Exception e) {
				resultMap.put("type", "error");
				resultMap.put("message", "请选择负责人");
				return resultMap;
			}
			String errMsg = taskVmService.publishTask(token.getObjectContext(),token.getSysUser(),ids,own,taskTime);
			if(StringUtil.isEmpty(errMsg)){
				resultMap.put("type", "success");
				resultMap.put("message", "发布成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", errMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("selectMtNodeDialog")
	public String selectMtNodeDialog(){
		
		return "deployTask/dialog/select_mt_node_dialog";
	}
	@RequestMapping("mtNodePage")
	public String mtNodePage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchNodeName){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/deployTask/mtNodePage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.mtNodePage(token.getObjectContext(),searchNodeName,pageSize,token.getSysUser());
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("mtNodeList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "deployTask/page/mt_node_page";
	}
	
}
