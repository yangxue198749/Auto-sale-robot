package com.kdx.vms.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmModelChannelService;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "vmModelChannel")
public class MtVmModelChannelController {
	
	@Resource
	private IMtVmModelChannelService iMtVmModelChannelService;
	
	@Resource
	private IMtVmModelService iMtVmModelService;
	
	@Resource(name = "mtVmService")
	private IMtVmService iMtVmService;
	
	@RequestMapping("createPage")
	@ResponseBody
	public ModelAndView createPage(Integer vmModelId){
		TMtVmModel tMtVmModel = iMtVmModelService.getTMtVmModel(String.valueOf(vmModelId), TokenUtil.getToken().getObjectContext());
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/add_mtVmModelchannel_dialog","tMtVmModel",tMtVmModel);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (String str : tMtVmModel.getChannelPerLayer().split(",")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("number", str);
			map.put("size", new BigDecimal(90.0/Integer.valueOf(str)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			list.add(map);
		}
		modelAndView.addObject("list", list);
		return modelAndView;
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updatePage(Integer ids){
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		TSysUser sysUser = TokenUtil.getToken().getSysUser();
		List<TMtVm> findMtVmList = iMtVmService.findMtVmList(context, null, null, ids, null, null, null, null, null, sysUser, 1, false);
		if(findMtVmList != null && findMtVmList.size() >0) return null;
		return createPage(ids);
	}
	
	@RequestMapping("rowPage")
	@ResponseBody
	public ModelAndView modelChannelRowPage(String args){
		String[] arg = args.split(",");
		String vmModelId = arg[0];
		String channelCode = arg[1];//当前行数
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/setRow_mtVmModelchannel_dialog","modelChannel",iMtVmModelChannelService.getVmModelChannelRowByCode(vmModelId, channelCode));
		modelAndView.addObject("channelCode", channelCode);
		modelAndView.addObject("vmModelId", vmModelId);
		return modelAndView;
	}
	
	@RequestMapping("setModelChannelRow")
	@ResponseBody
	public Integer updateModelChannel(Integer channelCode ,String vmModelId ,Integer channelLength ,Integer channelHeight ,Integer displayMode){
		return iMtVmModelChannelService.setModelChannelRow(channelCode, vmModelId, channelLength, channelHeight, displayMode, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("page")
	@ResponseBody
	public ModelAndView modelChannelPage(String args){
		String[] arg = args.split(",");
		String vmModelId = arg[0];
		String channelCode = arg[1];//货道编码
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/set_mtVmModelchannel_dialog","modelChannel",iMtVmModelChannelService.getVmModelChannelByCode(vmModelId, channelCode));
		modelAndView.addObject("channelCode", channelCode);
		modelAndView.addObject("vmModelId", vmModelId);
		return modelAndView;
	}
	
	@RequestMapping("setModelChannel")
	@ResponseBody
	public Integer updateModelChannel(String channelCode ,String vmModelId ,Integer channelWidth ,Integer acceptWidthMax ,Integer isFixedCapacity ,Integer fixedCapacity){
		return iMtVmModelChannelService.setModelChannel(channelCode, vmModelId, channelWidth, acceptWidthMax, isFixedCapacity, fixedCapacity, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("lookPage")
	@ResponseBody
	public ModelAndView lookPage(String ids){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		TMtVmModel tMtVmModel = iMtVmModelService.getTMtVmModel(ids, objectContext);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String[] cols = tMtVmModel.getChannelPerLayer().split(",");
		for (int i = 0; i < cols.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Integer colNum = Integer.valueOf(cols[i]);
			map.put("number", colNum);
			map.put("size", new BigDecimal(90.0/Integer.valueOf(colNum)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());//td大小
			List<TMtVmModelChannel> childList = new ArrayList<TMtVmModelChannel>();
			for (int j = 0; j < colNum; j++) {
				childList.add(iMtVmModelChannelService.getVmModelChannelByCode(ids, (i+1)+"-"+(j+1)));
			}
			map.put("childList", childList);
			list.add(map);
		}
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/look_mtVmModelchannel_dialog","tMtVmModel",tMtVmModel);
		modelAndView.addObject("list", list);
		return modelAndView;
	}

}
