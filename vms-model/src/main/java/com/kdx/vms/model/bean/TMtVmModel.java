package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtVmModel;
import org.apache.cayenne.Cayenne;

import java.util.Date;

public class TMtVmModel extends _TMtVmModel {

	public static final int COMBINATORIAL_SINGLE = 1;//单机
	public static final int COMBINATORIAL_MAIN = 2;//主机
	public static final int COMBINATORIAL_EXTENTION = 3;//副机

	public static final int SCREENHAVE_TRUE = 1;//有屏幕

	public static final int TEMPERATURE_HANDLER_MODE_NO = 50;//温控模式为无


	/**
	 * 默认售货机型号启用
	 */
	public static int DELETE_DEFAULT_MARK =0;
	
	private Integer id;
	
	public Integer getId(){
		if(id != null ) return id;
		return Cayenne.intPKForObject(this);
	}
	
	public void setId(Integer id){
		this.id = id;
	}

	public Integer getBoxCount(){
		return super.getTempBoxCount();
	}

	public void copyTMtVmModel(TMtVmModel vm){
//		this.setAllowMerge(vm.getAllowMerge());
//		this.setChannelLayerQty(vm.getChannelLayerQty());
//		this.setChannelPerLayer(vm.getChannelPerLayer());
		this.setCombinatorialPattern(vm.getCombinatorialPattern());
		this.setScreenHave(vm.getScreenHave());
		this.setModelType(vm.getModelType());
		this.setTempCtrlDesc(vm.getTempCtrlDesc());
		this.setChannelType(vm.getChannelType());
		this.setColingHdid(vm.getColingHdid());
		this.setCompanyInit(vm.getCompanyInit());
		this.setCoolingYn(vm.getCoolingYn());
		this.setElevatorPlatformHdid(vm.getElevatorPlatformHdid());
		this.setElevatorPlatformYn(vm.getElevatorPlatformYn());
		this.setHittingHdid(vm.getHittingHdid());
		this.setMachineView(vm.getMachineView());
		this.setModelDesc(vm.getModelDesc());
//		this.setModelName(vm.getModelName());
		this.setRfidCard(vm.getRfidCard());
		this.setScreenSize(vm.getScreenSize());
		this.setScreenType(vm.getScreenType());
//		this.setShortcutsQty(vm.getShortcutsQty());
		this.setTemperatureHandlerMode(vm.getTemperatureHandlerMode());
		this.setUtime(new Date());
		this.setVendOutArchitecture(vm.getVendOutArchitecture());
		this.setVendOutType(vm.getVendOutType());
		this.setTempBoxCount(vm.getTempBoxCount());
	}

	public String getCombinatorialPatternName(){
		if(null == getCombinatorialPattern()) return "";
		switch (getCombinatorialPattern()){
			case COMBINATORIAL_SINGLE:
				return "单机";
			case COMBINATORIAL_MAIN:
				return "主机";
			case COMBINATORIAL_EXTENTION:
				return "副机";
			default:
				return "";
		}
	}

}
