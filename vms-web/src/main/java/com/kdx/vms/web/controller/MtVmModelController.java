package com.kdx.vms.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.kdx.vms.orm.cache.DictCache;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Controller
@RequestMapping(value = "vmModel")
public class MtVmModelController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IMtVmModelService iMtVmModelService;
	
	@Resource(name = "mtVmService")
	private IMtVmService iMtVmService;
	
	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@RequestMapping("home")
	public String home(){
		return "mtVmModel/mtVmModel_list";
	}
	
	@RequestMapping("create")
	@ResponseBody
	public Integer createVmModel(TMtVmModel tMtVmModel){
		return iMtVmModelService.createVmModel(tMtVmModel, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("createPage")
	public ModelAndView createPage(){
//		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/add_mtVmModel_dialog");
		/*modelAndView.addObject("channelType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.CHANNEL_TYPE, objectContext));//货道类型
		modelAndView.addObject("companyInit", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.COMPANY_INIT, objectContext));//公司缩写
		modelAndView.addObject("machineView", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.MACHINE_VIEW, objectContext));//售货机外观
		modelAndView.addObject("temperatureHandlerMode", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.TEMPERATURE_HANDLER_MODE, objectContext));//温控模式
		modelAndView.addObject("screenType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.SCREEN_TYPE, objectContext));//屏种类
		modelAndView.addObject("screenSize", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.SCREEN_SIZE, objectContext));//屏尺寸
		modelAndView.addObject("vendOutArchitecture", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.VEND_OUT_ARCHITECTURE, objectContext));//出货结构
		modelAndView.addObject("coolingHdid", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.COOLING_HDID, objectContext));//制冷设备
		modelAndView.addObject("hittingHdid", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.HITTING_HDID, objectContext));//加热设备
		modelAndView.addObject("elevatorPlatformHdid", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.ELEVATOR_PLATFORM_HDID, objectContext));//云台设备
		modelAndView.addObject("vendOutType", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.VEND_OUT_TYPE, objectContext));//出货口
		modelAndView.addObject("rfidCard", iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.RFID_CARD, objectContext));//外接卡*/

		modelAndView.addObject("channelType", DictCache.getDictByCode(SysTypeinfoEnum.CHANNEL_TYPE.getType_code()));//货道类型
		modelAndView.addObject("companyInit", DictCache.getDictByCode(SysTypeinfoEnum.COMPANY_INIT.getType_code()));//公司缩写
		modelAndView.addObject("machineView", DictCache.getDictByCode(SysTypeinfoEnum.MACHINE_VIEW.getType_code()));//售货机外观
		modelAndView.addObject("temperatureHandlerMode", DictCache.getDictByCode(SysTypeinfoEnum.TEMPERATURE_HANDLER_MODE.getType_code()));//温控模式
		modelAndView.addObject("screenType", DictCache.getDictByCode(SysTypeinfoEnum.SCREEN_TYPE.getType_code()));//屏种类
		modelAndView.addObject("screenSize", DictCache.getDictByCode(SysTypeinfoEnum.SCREEN_SIZE.getType_code()));//屏尺寸
		modelAndView.addObject("vendOutArchitecture", DictCache.getDictByCode(SysTypeinfoEnum.VEND_OUT_ARCHITECTURE.getType_code()));//出货结构
		modelAndView.addObject("coolingHdid", DictCache.getDictByCode(SysTypeinfoEnum.COOLING_HDID.getType_code()));//制冷设备
		modelAndView.addObject("hittingHdid", DictCache.getDictByCode(SysTypeinfoEnum.HITTING_HDID.getType_code()));//加热设备
		modelAndView.addObject("elevatorPlatformHdid", DictCache.getDictByCode(SysTypeinfoEnum.ELEVATOR_PLATFORM_HDID.getType_code()));//云台设备
		modelAndView.addObject("vendOutType", DictCache.getDictByCode(SysTypeinfoEnum.VEND_OUT_TYPE.getType_code()));//出货口
		modelAndView.addObject("rfidCard", DictCache.getDictByCode(SysTypeinfoEnum.RFID_CARD.getType_code()));//外接卡
		modelAndView.addObject("modelType", DictCache.getDictByCode(SysTypeinfoEnum.MODEL_TYPE.getType_code()));//机型分类
		return modelAndView;
	}
	
	
	@RequestMapping("update")
	@ResponseBody
	public Integer updateVmModel(TMtVmModel tMtVmModel){
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		TSysUser sysUser = TokenUtil.getToken().getSysUser();
		List<TMtVm> findMtVmList = iMtVmService.findMtVmList(context, null, null, Integer.valueOf(tMtVmModel.getId()), null, null, null, null, null, sysUser, 1, false);
		if(findMtVmList != null && findMtVmList.size() >0) return null;
		return iMtVmModelService.updateVmModel(tMtVmModel, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	@RequestMapping("updatePage")
	@ResponseBody
	public ModelAndView updateVmModelPage(String ids){
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		ModelAndView modelAndView = new ModelAndView("mtVmModel/dialog/edit_mtVmModel_dialog","vmModel" ,iMtVmModelService.getTMtVmModel(ids, objectContext));
		modelAndView.addObject("channelType", DictCache.getDictByCode(SysTypeinfoEnum.CHANNEL_TYPE.getType_code()));//货道类型
		modelAndView.addObject("companyInit", DictCache.getDictByCode(SysTypeinfoEnum.COMPANY_INIT.getType_code()));//公司缩写
		modelAndView.addObject("machineView", DictCache.getDictByCode(SysTypeinfoEnum.MACHINE_VIEW.getType_code()));//售货机外观
		modelAndView.addObject("temperatureHandlerMode", DictCache.getDictByCode(SysTypeinfoEnum.TEMPERATURE_HANDLER_MODE.getType_code()));//温控模式
		modelAndView.addObject("screenType", DictCache.getDictByCode(SysTypeinfoEnum.SCREEN_TYPE.getType_code()));//屏种类
		modelAndView.addObject("screenSize", DictCache.getDictByCode(SysTypeinfoEnum.SCREEN_SIZE.getType_code()));//屏尺寸
		modelAndView.addObject("vendOutArchitecture", DictCache.getDictByCode(SysTypeinfoEnum.VEND_OUT_ARCHITECTURE.getType_code()));//出货结构
		modelAndView.addObject("coolingHdid", DictCache.getDictByCode(SysTypeinfoEnum.COOLING_HDID.getType_code()));//制冷设备
		modelAndView.addObject("hittingHdid", DictCache.getDictByCode(SysTypeinfoEnum.HITTING_HDID.getType_code()));//加热设备
		modelAndView.addObject("elevatorPlatformHdid", DictCache.getDictByCode(SysTypeinfoEnum.ELEVATOR_PLATFORM_HDID.getType_code()));//云台设备
		modelAndView.addObject("vendOutType", DictCache.getDictByCode(SysTypeinfoEnum.VEND_OUT_TYPE.getType_code()));//出货口
		modelAndView.addObject("rfidCard", DictCache.getDictByCode(SysTypeinfoEnum.RFID_CARD.getType_code()));//外接卡
		modelAndView.addObject("modelType", DictCache.getDictByCode(SysTypeinfoEnum.MODEL_TYPE.getType_code()));//机型分类
		return modelAndView;
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Boolean deleteVmModel(String ids){
		//得判断下是否应用过
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		TSysUser sysUser = TokenUtil.getToken().getSysUser();
		List<TMtVm> findMtVmList = iMtVmService.findMtVmList(context, null, null, Integer.valueOf(ids), null, null, null, null, null, sysUser, 1, false);
		if(findMtVmList != null && findMtVmList.size() >0) return false;
		return iMtVmModelService.deleteVmModel(ids, context);
	}
	
	@RequestMapping("list")
	@ResponseBody
	public ModelAndView list(String name ,Integer pageSize ,Integer pageNo){
		return new ModelAndView("mtVmModel/page/mtVmModel_page",iMtVmModelService.list(name, pageSize, pageNo, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("lookOtherPage")
	@ResponseBody
	public ModelAndView lookOtherPage(String id){
		return new ModelAndView("mtVmModel/dialog/lookOther_mtVmModel_dialog" ,"vmModel" ,iMtVmModelService.getTMtVmModel(id, TokenUtil.getToken().getObjectContext()));
	}
	
	@RequestMapping("getTMtVmModel")
	@ResponseBody
	public TMtVmModel getTMtVmModel(String id){
		return iMtVmModelService.getTMtVmModel(id, TokenUtil.getToken().getObjectContext());
	}

	@RequestMapping("lockedOrUnlocked")
	@ResponseBody
	public Map<String,Object> lockedOrUnlocked(String ids){
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		return iMtVmModelService.lockedOrUnlocked(ids,context);
	}

	@RequestMapping("validateModelScreenHave")
	@ResponseBody
	public Map<String, Object> validateModelScreenHave(Integer[] ids) {
		Map<String, Object> resultmMap = new HashMap<>();
		resultmMap.put("type", "success");
		try {
			SSOToken token = TokenUtil.getToken();
			ObjectContext context = token.getObjectContext();
			for(Integer vmId:ids){
				TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
				TMtVmModel tMtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, firstMtVm.getVmModelId());
				if (null == tMtVmModel.getScreenHave() || !tMtVmModel.getScreenHave().equals(TMtVmModel.SCREENHAVE_TRUE)){
					resultmMap.put("type", "error");
					resultmMap.put("message", "该机型无屏幕");
					return resultmMap;
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			resultmMap.put("type", "error");
			resultmMap.put("message", "该机型无屏幕");
			return resultmMap;
		}
		return resultmMap;
	}
}
