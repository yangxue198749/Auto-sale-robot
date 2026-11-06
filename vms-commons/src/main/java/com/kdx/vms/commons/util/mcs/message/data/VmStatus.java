package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
import java.util.List;

public class VmStatus implements Serializable{

	private static final long serialVersionUID = -3530079890447581019L;
	private String vmStatus;// 售货机运行状态（'0'表示正常，'1'表示故障，'2'表示忙碌）
	private String tempValue;// 设备温度
	private boolean verticalStepperMoto;// 垂直步进电机状态
	private boolean horizontalMotoStepperMoto;// 水平步进电机状态
	private boolean  slotMoto;// 取物门电机状态
	private boolean leftPickMoto;// 左侧取货电机状态
	private boolean  rightPickMoto;// 右侧取货电机状态
	private boolean  leftRotateMoto;// 左侧旋转电机状态
	private boolean  rightRotateMoto;// 右侧旋转电机状态
    private boolean  shipSensor;// 出货传器状态
    private boolean  mdseOnPlatform;// 取物台是否有货物
    private boolean outputSlot;// 取物门开关状态
    private List<String> channelMotorFailureCodes;//有故障的货道电机编码（当前数组只输出有故障的货道电机编号，如果所有电机都正常，当前为长度 为0的空数组）
	public String getVmStatus() {
		return vmStatus;
	}
	public void setVmStatus(String vmStatus) {
		this.vmStatus = vmStatus;
	}
	public String getTempValue() {
		return tempValue;
	}
	public void setTempValue(String tempValue) {
		this.tempValue = tempValue;
	}
	public boolean isVerticalStepperMoto() {
		return verticalStepperMoto;
	}
	public void setVerticalStepperMoto(boolean verticalStepperMoto) {
		this.verticalStepperMoto = verticalStepperMoto;
	}
	public boolean isHorizontalMotoStepperMoto() {
		return horizontalMotoStepperMoto;
	}
	public void setHorizontalMotoStepperMoto(boolean horizontalMotoStepperMoto) {
		this.horizontalMotoStepperMoto = horizontalMotoStepperMoto;
	}
	public boolean isSlotMoto() {
		return slotMoto;
	}
	public void setSlotMoto(boolean slotMoto) {
		this.slotMoto = slotMoto;
	}
	public boolean isLeftPickMoto() {
		return leftPickMoto;
	}
	public void setLeftPickMoto(boolean leftPickMoto) {
		this.leftPickMoto = leftPickMoto;
	}
	public boolean isRightPickMoto() {
		return rightPickMoto;
	}
	public void setRightPickMoto(boolean rightPickMoto) {
		this.rightPickMoto = rightPickMoto;
	}
	public boolean isLeftRotateMoto() {
		return leftRotateMoto;
	}
	public void setLeftRotateMoto(boolean leftRotateMoto) {
		this.leftRotateMoto = leftRotateMoto;
	}
	public boolean isRightRotateMoto() {
		return rightRotateMoto;
	}
	public void setRightRotateMoto(boolean rightRotateMoto) {
		this.rightRotateMoto = rightRotateMoto;
	}
	public boolean isShipSensor() {
		return shipSensor;
	}
	public void setShipSensor(boolean shipSensor) {
		this.shipSensor = shipSensor;
	}
	public boolean isMdseOnPlatform() {
		return mdseOnPlatform;
	}
	public void setMdseOnPlatform(boolean mdseOnPlatform) {
		this.mdseOnPlatform = mdseOnPlatform;
	}
	public boolean isOutputSlot() {
		return outputSlot;
	}
	public void setOutputSlot(boolean outputSlot) {
		this.outputSlot = outputSlot;
	}
	public List<String> getChannelMotorFailureCodes() {
		return channelMotorFailureCodes;
	}
	public void setChannelMotorFailureCodes(List<String> channelMotorFailureCodes) {
		this.channelMotorFailureCodes = channelMotorFailureCodes;
	}
}
