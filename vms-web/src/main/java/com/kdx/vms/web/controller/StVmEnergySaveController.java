package com.kdx.vms.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmStrategy;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmEnergySave;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.service.IStVmEnergySaveService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;

/**
 * 节能策略
 * @author zhangguodong
 *
 */
@Controller
@RequestMapping("stVmEnergySave")
public class StVmEnergySaveController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IMtVmModelService mtVmModelService;
	@Resource
	private IStVmEnergySaveService stVmEnergySaveService;
	
	@RequestMapping("list")
	public String list(HttpServletRequest request){
		
		try {
			SSOToken token = TokenUtil.getToken();
			List vmModelList = mtVmModelService.findMtVmModel(token.getObjectContext(), false);
			request.setAttribute("vmModelList",vmModelList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/st_vm_energy_save_list";
	}
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchStVmName,Integer searchVmModelId){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/stVmEnergySave/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = stVmEnergySaveService.findPage(token.getObjectContext(), token.getSysUser(), searchStVmName, searchVmModelId, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("stVmStrategyList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "stVmEnergySave/page/st_vm_energy_save_page";
	}
	
	@RequestMapping("addStVmEnergyDialog")
	public String addStVmEnergyDialog(HttpServletRequest request){
		try {
			SSOToken token = TokenUtil.getToken();
			List vmModelList = mtVmModelService.findMtVmModel(token.getObjectContext(), false);
			request.setAttribute("vmModelList",vmModelList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "stVmEnergySave/dialog/add_st_vm_energy_save_dialog";
	}
	
	@RequestMapping("saveAndNext")
	@ResponseBody
	public Map<String,Object> saveAndNext(String addStVmName,String addStVmDesc,Integer addVmModelId){
		Map<String,Object> resultMap = new HashMap<>();
		try {
			SSOToken token = TokenUtil.getToken();
			resultMap = stVmEnergySaveService.addStVmStrategy(token.getObjectContext(), addStVmName, addStVmDesc, addVmModelId, token.getSysUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return resultMap;
		
	}
	
	
	@RequestMapping("addEnergyDialog")
	public String addEnergyDialog(HttpServletRequest request,Integer strategyId){
		try {
			TStVmStrategy stVmStrategy = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(),TStVmStrategy.class,strategyId);
			if(null!=stVmStrategy){
				TMtVmModel mtVmModel = stVmStrategy.getVmModel();
				request.setAttribute("mtVmModel", mtVmModel);
			}
			request.setAttribute("strategyId", strategyId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/add_energy_save_dialog";
	}
	@RequestMapping("addEnergySave")
	@ResponseBody
	public Map<String,Object> addEnergySave(Integer addTargetValue,Integer addEnergySaveType,String addStartTime,String addEndTime,String addRemark,Integer strategyId,Integer addTempBoxNo){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			
			resultMap = stVmEnergySaveService.addStVmEnergySave(token.getObjectContext(),token.getSysUser(),strategyId,addTargetValue,addEnergySaveType,addStartTime,addEndTime,addRemark,addTempBoxNo);
			if("success".equals(resultMap.get("type"))){
				String html = stVmEnergySaveService.makeTableString(token.getObjectContext(), strategyId);
				resultMap.put("html", html);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	
	@RequestMapping("deleteStVmStrategy")
	@ResponseBody
	public Map<String,Object> deleteStVmStrategy(Integer[] ids){
		Map<String,Object> resultMap = new HashMap<>();
		try {
			SSOToken token = TokenUtil.getToken();
			
			String errMsg = stVmEnergySaveService.deleteStVmEnergySaveByStrategyId(token.getObjectContext(), ids, token.getSysUser());
			resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"erroe");
			resultMap.put("message", StringUtil.isEmpty(errMsg)?"删除成功":errMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	
	/**
	 * 删除节能详情
	 * @param ids
	 * @return
	 */
	@RequestMapping("deleteEnergySave")
	@ResponseBody
	public Map<String,Object> deleteEnergySave(Integer[] ids){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			
			String errMsg = stVmEnergySaveService.deleteEnergySave(token.getObjectContext(), ids, token.getSysUser());
			resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"erroe");
			resultMap.put("message", StringUtil.isEmpty(errMsg)?"删除成功":errMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("addEditEnergySaveDialog")
	public String addEditEnergySaveDialog(HttpServletRequest request,Integer ids){
		try {
			SSOToken token = TokenUtil.getToken();
			TStVmEnergySave energySave = stVmEnergySaveService.findByEnergySaveId(token.getObjectContext(),ids);
			request.setAttribute("energySaveId", ids);
			request.setAttribute("energySave", energySave);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/add_edit_energy_save_dialog";
	}
	@RequestMapping("addEditEnergySave")
	@ResponseBody
	public Map<String,Object> addEditEnergySave(Integer addTargetValue,Integer addEnergySaveType,String addStartTime,String addEndTime,String addRemark,Integer energySaveId,Integer addTempBoxNo){
		Map<String,Object> resultMap = new HashMap<>();
		try {
			SSOToken token = TokenUtil.getToken();
			
			resultMap = stVmEnergySaveService.editEnergySave(token.getObjectContext(),token.getSysUser(),energySaveId,addTargetValue,addEnergySaveType,addStartTime,addEndTime,addRemark,addTempBoxNo);
			if("success".equals(resultMap.get("type"))){
				String html = stVmEnergySaveService.makeTableString(token.getObjectContext(), Integer.parseInt(resultMap.get("strategyId")+""));
				resultMap.put("html", html);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("showStVmEnergySave")
	public String showStVmEnergySave(HttpServletRequest request,Integer strategyId){
		try {
			SSOToken token = TokenUtil.getToken();
			String html = stVmEnergySaveService.makeTableString(token.getObjectContext(), strategyId);
			request.setAttribute("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/show_st_vm_energy_save_dialog";
		
	}
	
	@RequestMapping("editStVmEnergySaveDialog")
	public String editStVmEnergySaveDialog(HttpServletRequest request,Integer ids){
		try {
			SSOToken token = TokenUtil.getToken();
			String html = stVmEnergySaveService.makeTableString(token.getObjectContext(), ids);
			request.setAttribute("html", html);
			request.setAttribute("strategyId", ids);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/edit_st_vm_energy_save_dialog";
		
	}
	
	@RequestMapping("editAddEnergyDialog")
	public String editAddEnergyDialog(HttpServletRequest request,Integer strategyId){
		try {
			request.setAttribute("strategyId", strategyId);
			TStVmStrategy stVmStrategy = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(),TStVmStrategy.class,strategyId);
			if(null!=stVmStrategy){
				TMtVmModel mtVmModel = stVmStrategy.getVmModel();
				request.setAttribute("mtVmModel", mtVmModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/edit_add_energy_save_dialog";
		
	}
	@RequestMapping("editEditEnergySaveDialog")
	public String editEditEnergySaveDialog(HttpServletRequest request,Integer ids){
		try {
			SSOToken token = TokenUtil.getToken();
			TStVmEnergySave energySave = stVmEnergySaveService.findByEnergySaveId(token.getObjectContext(),ids);
			request.setAttribute("energySaveId", ids);
			request.setAttribute("energySave", energySave);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmEnergySave/dialog/edit_edit_energy_save_dialog";
		
	}
	
	
}

