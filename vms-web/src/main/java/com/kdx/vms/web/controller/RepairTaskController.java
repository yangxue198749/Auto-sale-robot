package com.kdx.vms.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMcsService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;

/**
 * 维护工单
 * @author zhangguodong
 *
 */
@RequestMapping("repairTask")
@Controller
public class RepairTaskController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private ITaskVmService taskVmService;
	@Resource
	private ISysUserService sysUserService;
	@Autowired
	private IMcsService mcsService;
	@RequestMapping("list")
	public String list(){
		
		return "repairTask/repair_task_list";
	}
	
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer searchField,String searchFieldValue,Integer searchTaskVmStatus,String startTime,String endTime){
		
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/repairTask/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.findPage(token.getObjectContext(), token.getSysUser(), searchField, searchFieldValue, searchTaskVmStatus, startTime, endTime, TTaskVm.TYPE_REPAIR, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("taskVmList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "repairTask/page/repair_task_page";
	}
	
	@RequestMapping("addTaskDialog")
	public String addTaskDialog(){
		
		return "repairTask/dialog/add_task_dialog";
	}
	@RequestMapping("addTask")
	@ResponseBody
	public Map<String,Object> addTask(Integer vmId,String ownerId,Integer defaultUserId,String remarks,String taskTime){
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
			String errMsg = taskVmService.createRepairTask(token.getObjectContext(),token.getSysUser(),vmId,own,remarks,taskTime);
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
				new Thread(() -> {
					ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
					Arrays.stream(ids)
							.map(id -> Cayenne.objectForPK(context, TTaskVm.class, id))
							.map(TTaskVm::getMtVm)
							.map(TMtVm::getId)
							.forEach(vmId -> mcsService.notifyOpenDoorPwd(context, vmId));
				}).start();
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
		
		return "repairTask/dialog/select_mt_vm_dialog";
	}
	@RequestMapping("editTaskDialog")
	public String editTaskDialog(HttpServletRequest request,Integer ids){
		try {
			SSOToken token = TokenUtil.getToken();
			TTaskVm taskVm = taskVmService.findByPk(token.getObjectContext(),ids);
			if(taskVm != null){
				String html = sysUserService.findSysUsersByRoleLevelForCompany(token.getObjectContext(), TSysRole.ROLE_LEVEL_REPAIR, token.getSysUser().getCompany().getId(), taskVm.getOwner());
				request.setAttribute("taskVm", taskVm);
				request.setAttribute("html", html);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "repairTask/dialog/edit_task_vm_dialog";
	}
	@RequestMapping("mtVmPage")
	public String mtVmPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchNodeName,String searchInnerCode){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/repairTask/mtVmPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.mtVmPage(token.getObjectContext(),token.getSysUser(),searchNodeName,searchInnerCode,pageSize,null);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "repairTask/page/mt_vm_page";
	}
	@RequestMapping("editTaskVm")
	@ResponseBody
	public Map<String,Object> editTaskVm(Integer taskVmId,String editOwnerId,String remarks,String taskTime){
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
			String errMsg = taskVmService.editTaskVm(token.getObjectContext(),token.getSysUser(),taskVmId,own,remarks,taskTime);
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
}
