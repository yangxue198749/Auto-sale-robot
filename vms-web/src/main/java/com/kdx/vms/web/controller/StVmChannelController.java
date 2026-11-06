package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmChannel;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.orm.dao.MtSkuTypeDao;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.service.ISkuTypeService;
import com.kdx.vms.service.IStVmChannelService;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 售货机货道策略
 * @author zhangguodong
 *
 */
@Controller
@RequestMapping("stVmChannel")
public class StVmChannelController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IMtVmModelService mtVmModelService;
	@Resource
	private IStVmChannelService stVmChannelService;
	@Resource
	private ISkuTypeService skuTypeService;
	@Resource
	private MtSkuTypeDao mtSkuTypeDao;
	@Resource
	private IStVmStrategyService stVmStrategyService;
	@RequestMapping("list")
	public String list(HttpServletRequest request){
		try {
			SSOToken token = TokenUtil.getToken();
			request.setAttribute("vmModelList",mtVmModelService.findMtVmModel(token.getObjectContext(), false));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/st_vm_channel_list";
	}
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String searchStVmName,Integer searchVmModelId){
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/stVmChannel/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = stVmChannelService.getPage(token.getObjectContext(), token.getSysUser(), searchStVmName, searchVmModelId, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("stVmStrategyList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		
		return "stVmChannel/page/st_vm_channel_page";
	}
	
	@RequestMapping("addStVmChannelDialog")
	public String addStVmChannelDialog(HttpServletRequest request){
		try {
			SSOToken token = TokenUtil.getToken();
			request.setAttribute("vmModelList",mtVmModelService.findMtVmModel(token.getObjectContext(), false));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/add_st_vm_channel_dialog";
		
	}
	
	@RequestMapping("addStVmChannelDetailDialog")
	@ResponseBody
	public Map<String,Object> addStVmChannelDetailDialog(HttpServletRequest request,Integer addVmModelId){
		Map<String,Object>resultMap = new HashMap<String,Object>();
		try {
	           String addStVmName = request.getParameter("addStVmName");
	           String addStVmDesc = request.getParameter("addStVmDesc");
	           SSOToken token = TokenUtil.getToken();
	           String result = stVmChannelService.addStVmChannel(token.getObjectContext(), token.getSysUser(), addVmModelId, addStVmName, addStVmDesc);
	           resultMap.put("html", result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		
		return resultMap;
	}
	@RequestMapping("showStVmChannelDialog")
	public String showStVmChannelDialog (HttpServletRequest request,Integer strategyId){
		
		try {
			
			SSOToken token = TokenUtil.getToken();
			String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), strategyId, true);
			request.setAttribute("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/show_st_vm_channel_dialog";
	}
	@RequestMapping("editStVmChannelDialog")
	public String editStVmChannelDialog(HttpServletRequest request,String ids){
		try {
				SSOToken token = TokenUtil.getToken();
			String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), Integer.parseInt(ids), false);
			request.setAttribute("html", html);
			request.setAttribute("strategyId", ids);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/edit_st_vm_channel_dialog";
		
	}
	@RequestMapping("mergeChannel")
	@ResponseBody
	public Map<String,Object> mergeChannel(Integer firstChannelId,Integer secondChannelId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			String errMsg = stVmChannelService.mergeChannel(token.getObjectContext(), firstChannelId, secondChannelId, token.getSysUser());
			if(StringUtil.isEmpty(errMsg)){
				TStVmChannel channel = stVmChannelService.findByPk(token.getObjectContext(),firstChannelId);
				String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), channel.getStrategy().getStrategyId(), false);
				resultMap.put("type", "success");
				resultMap.put("html", html);
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
	@RequestMapping("cancelMerge")
	@ResponseBody
	public Map<String,Object> cancelMerge(Integer channelId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			
			SSOToken token = TokenUtil.getToken();
			String errMsg = stVmChannelService.cancelMergeChannel(token.getObjectContext(), channelId, token.getSysUser());
			if(StringUtil.isEmpty(errMsg)){
				TStVmChannel channel = stVmChannelService.findByPk(token.getObjectContext(),channelId);
				String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), channel.getStrategy().getStrategyId(), false);
				resultMap.put("type", "success");
				resultMap.put("html", html);
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
	@RequestMapping("selectSkuDialog")
	public String selectSkuDialog(HttpServletRequest request,String ids,String relateType,Integer relateStartegyId){
		try {
			request.setAttribute("relateChannelIds", ids);
			request.setAttribute("relateType", relateType);
			request.setAttribute("categoryType", relateType);
			request.setAttribute("relateStartegyId", relateStartegyId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/select_sku_dialog";
	}
	@RequestMapping("skuList")
	public String skuList(HttpServletRequest request,Integer skuTypeId,Integer skuTypeGroupId,String skuName, String skuCode){
//		try {
//			List skuList = SkuUtils.getSkuList(skuTypeId, skuTypeGroupId, new Integer[0], skuName,false);
//			request.setAttribute("skuList", skuList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		request.setAttribute("skuIds","");
		request.setAttribute("skuName",skuName);
		request.setAttribute("skuCode",skuCode);
		request.setAttribute("skuGroupTypeId",skuTypeGroupId);
		request.setAttribute("skuTypeId",skuTypeId);
		return "stVmChannel/page/select_sku_page";
	}



	@RequestMapping("relateSku")
	@ResponseBody
	public Map<String,Object> relateSku(Integer[] channelIds,Integer skuId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			resultMap = stVmChannelService.relateSku(token.getObjectContext(), token.getSysUser(), channelIds, skuId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("cancelRelate")
	@ResponseBody
	public Map<String,Object> cancelRelate(Integer[] channelIds){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			String errMsg = stVmChannelService.cancelRelateSku(token.getObjectContext(), token.getSysUser(), Arrays.asList(channelIds), true);
			if(StringUtil.isEmpty(errMsg)){
				TStVmChannel channel = stVmChannelService.findByPk(token.getObjectContext(), channelIds[0]);
				if(null != channel && null != channel.getStrategy()){
					String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), channel.getStrategy().getStrategyId(), false);
					resultMap.put("type", "success");
					resultMap.put("html", html);
				}
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
	@RequestMapping("getChannelDetail")
	@ResponseBody
	public Map<String ,Object> getChannelDetail(Integer strategyId){
		Map<String ,Object> resultMap = new HashMap<String ,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			String html = stVmChannelService.makeStVmChanenlTableString(token.getObjectContext(), strategyId, false);
			resultMap.put("html", html);
			resultMap.put("type", "success");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
			resultMap.put("type", "error");
		}
		return resultMap;
	}
	@RequestMapping("setSortDialog")
	public String setSortDialog(HttpServletRequest request,Integer strategyId){
		try {
			SSOToken token = TokenUtil.getToken();
			String html = stVmChannelService.makeStringSort(token.getObjectContext(), strategyId);
			request.setAttribute("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/set_sort_dialog";
	}
	@RequestMapping("changeSort")
	@ResponseBody
	public Map<String,Object> changeSort (Integer firstSortId,Integer secondSortId){
		Map<String,Object> resultMap = new HashMap<String,Object>(); 
		try {
			SSOToken token = TokenUtil.getToken();
			resultMap = stVmChannelService.changeSort(token.getObjectContext(),token.getSysUser(),firstSortId,secondSortId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
	}
	@RequestMapping("showSortDialog")
	public String showSortDialog(HttpServletRequest request,Integer strategyId){
		try {
			SSOToken token = TokenUtil.getToken();
			String html = stVmChannelService.makeStringSort(token.getObjectContext(), strategyId);
			request.setAttribute("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "stVmChannel/dialog/show_sort_dialog";
	}
	@RequestMapping("deleteStrategy")
	@ResponseBody
	public Map<String,Object> deleteStrategy(Integer[] ids){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			Map<String, Object> map = stVmStrategyService.deleteStrategy(token.getSysUser(), token.getObjectContext(), ids, TStVmStrategy.TYPE_ST_VM_CHANNEL);
			if(0==(Integer)map.get("code")){
				resultMap.put("type", "success");
				resultMap.put("message", "删除成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", "删除失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return resultMap;
		
	}
	@RequestMapping("copySort")
	@ResponseBody
	public Map<String,Object> copySort(Integer sortId){

		try {
			SSOToken token = TokenUtil.getToken();
			ObjectContext context = token.getObjectContext();
			return stVmChannelService.copySort(context,token.getSysUser(),sortId);
		}catch (Exception e){
			logger.error(e.getLocalizedMessage(),e);
		}
		return ReturnCode.FAIL.toMap();
	}

	@RequestMapping("deleteSort")
	@ResponseBody
	public Map<String,Object> deleteSort(Integer sortId){

		try {
			SSOToken token = TokenUtil.getToken();
			ObjectContext context = token.getObjectContext();
			return stVmChannelService.deleteSort(context,token.getSysUser(),sortId);
		}catch (Exception e){
			logger.error(e.getLocalizedMessage(),e);
		}
		return ReturnCode.FAIL.toMap();
	}
	@RequestMapping("showFullDialog")
	public String showFullDialog(HttpServletRequest request, Integer channelId){
		request.setAttribute("channelId",channelId);
		return "stVmChannel/dialog/set_full_sku_dialog";
	}

	@RequestMapping("editSkuFull")
	@ResponseBody
	public Map<String,Object> editSkuFull(Integer channelId, Integer fullval){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			SSOToken token = TokenUtil.getToken();
			ObjectContext context = token.getObjectContext();
			Objects.nonNull(channelId);
			Objects.nonNull(fullval);
			resultMap = stVmChannelService.setFullSku(context, channelId, fullval);
		}catch (Exception e){
			logger.error(e.getLocalizedMessage(),e);
			resultMap.put("type","error");
			resultMap.put("message","货道或满量不能为空");

		}
		return resultMap;
	}
}
