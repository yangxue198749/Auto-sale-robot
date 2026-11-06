package com.kdx.vms.commons.util.mcs.message;

import com.kdx.vms.commons.util.mcs.message.data.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyang on 2016/11/8.
 */
public class Data implements Serializable {

    private static final long serialVersionUID = 1815843741570515842L;
    private String innerCode;
    //5.0新增副机号
    private Integer extentionCode;
    private String msgType;
    private String openDoorPwd;
    private List<ChannelCfg> channelCfgs;
    private List<SkuCfg> skuCfgs;
    private List<SpCfg> spCfgs;
    private List<SkuPrice> skuPrices;
    //新增保温箱数量
    private Integer tempBoxCount;
    private List<MtEnergy> mtEnergys;
    private RepeatText repeatText;
    private OutResult outResult;
    private OutSkus outSkus;
    private String macAddress;
    private String simCode;
    private VmStatus status;
    private List<Supply> supply;
    private String phone;
    private String id;
    private AreaRelation areaRelation;
    private VmBaseInfo vmBaseInfo;

    //5.0.0a新增
    private List<Extentions> extentions;
    private List<Portalcfg> portalcfgs;
    private List<Extention> extentionList;
    private Integer vmType;

    private List<SkuOrder> skuOrder;
    private Long versionNumber;

    public Integer getTempBoxCount() {
        return tempBoxCount;
    }

    private DeviceInfo deviceInfo;

    private Boolean doorStatus;
    private String time;

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public Integer getExtentionCode() {
        return extentionCode;
    }

    public Data setExtentionCode(Integer extentionCode) {
        this.extentionCode = extentionCode;
        return this;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType.getName();
    }

    public List<ChannelCfg> getChannelCfgs() {
        return channelCfgs;
    }

    public Integer getVmType() {
        return vmType;
    }

    public Data setVmType(Integer vmType) {
        this.vmType = vmType;
        return this;
    }

    public void addChannelCfgs(String channelCode, Integer maxCapcity, Integer skuId) {
        ChannelCfg channelCfg = new ChannelCfg();
        channelCfg.setChannelCode(channelCode);
        channelCfg.setMaxCapcity(maxCapcity);
        channelCfg.setSkuId(skuId);
        addChannelCfgs(channelCfg);
    }

    public void addSupply(String channelCode, Integer currentCount) {
        Supply sup = new Supply();
        sup.setChannelCode(channelCode);
        sup.setCurrentCount(currentCount);
        addSupply(sup);
    }

    public void addSupply(Supply sup) {
        if (null == this.supply)
            this.supply = new ArrayList<>();
        this.supply.add(sup);

    }

    public void addChannelCfgs(ChannelCfg channelCfg) {
        if (null == this.channelCfgs)
            this.channelCfgs = new ArrayList<>();
        this.channelCfgs.add(channelCfg);
    }

    public List<SkuCfg> getSkuCfgs() {
        return skuCfgs;
    }

    public void setSkuCfgs(List<SkuCfg> skuCfgs) {
        this.skuCfgs = skuCfgs;
    }

    public void addSkuCfgs(Integer skuId, Integer offerPrice, Integer index, String skuImageUrl, String skuName, String className, Integer classId, String unit, String desc, String brand) {
        SkuCfg skuCfg = new SkuCfg();
        skuCfg.setSkuId(skuId);
        skuCfg.setOfferPrice(offerPrice);
        skuCfg.setIndex(index);
        skuCfg.setSkuImageUrl(skuImageUrl);
        skuCfg.setSkuName(skuName);
        skuCfg.setClassName(className);
        skuCfg.setClassId(classId);
        skuCfg.setUnit(unit);
        skuCfg.setDesc(desc);
        skuCfg.setBrand(brand);
        addSkuCfgs(skuCfg);
    }

    public void addSkuCfgs(SkuCfg skuCfg) {
        if (null == this.skuCfgs)
            this.skuCfgs = new ArrayList<>();
        this.skuCfgs.add(skuCfg);
    }

    public List<SpCfg> getSpCfgs() {
        return spCfgs;
    }

    public void addSpCfgs(String spCode, Integer skuId) {
        SpCfg spCfg = new SpCfg();
        spCfg.setSpCode(spCode);
        spCfg.setSkuId(skuId);
        addSpCfgs(spCfg);
    }

    public void addSpCfgs(SpCfg spCfg) {
        if (null == this.spCfgs)
            this.spCfgs = new ArrayList<>();
        this.spCfgs.add(spCfg);
    }

    public List<SkuPrice> getSkuPrices() {
        return skuPrices;
    }

    public void addSkuPrices(Integer skuId, Integer price, Boolean isSales) {
        SkuPrice skuPrice = new SkuPrice();
        skuPrice.setSkuId(skuId);
        skuPrice.setPrice(price);
        skuPrice.setIsDiscount(isSales);
        addSkuPrices(skuPrice);
    }

    public void addSkuPrices(SkuPrice skuPrice) {
        if (null == this.skuPrices)
            this.skuPrices = new ArrayList<>();
        this.skuPrices.add(skuPrice);
    }

    public List<MtEnergy> getMtEnergys() {
        return mtEnergys;
    }

    public void addMtEnergys(Integer energyType, Integer targetValue, String startTime, String endTime) {
        MtEnergy mtEnergy = new MtEnergy();
        mtEnergy.setEnergyType(energyType);
        mtEnergy.setTargetValue(targetValue);
        mtEnergy.setStartTime(startTime);
        mtEnergy.setEndTime(endTime);
        addMtEnergys(mtEnergy);
    }

    public void addMtEnergys(MtEnergy mtEnergy) {
        if (null == this.mtEnergys)
            this.mtEnergys = new ArrayList<>();
        this.mtEnergys.add(mtEnergy);
    }

    public void setRepeatText(String text, String startTime, String endTime) {
        RepeatText repeatText = new RepeatText();
        repeatText.setText(text);
        repeatText.setStartTime(startTime);
        repeatText.setEndTime(endTime);
        this.repeatText = repeatText;
    }

    public void setRepeatText(RepeatText repeatText) {
        this.repeatText = repeatText;
    }

    public RepeatText getRepeatText() {
        return repeatText;
    }

    public void setOutResult(String orderId, String channelCode, String outTime, Integer outStatus) {
        OutResult outResult = new OutResult();
        outResult.setOrderId(orderId);
        outResult.setChannelCode(channelCode);
        outResult.setOutTime(outTime);
        outResult.setOutStatus(outStatus);
        this.outResult = outResult;
    }

    public void setOutResult(OutResult outResult) {
        this.outResult = outResult;
    }

    public OutResult getOutResult() {
        return outResult;
    }

    public void setOutSku(Long requestTime, Integer skuId, String orderId, Integer timeOut, Integer payPrice) {
        OutSkus outSkus = new OutSkus();
        outSkus.setRequestTime(requestTime);
        outSkus.setSkuId(skuId);
        outSkus.setOrderId(orderId);
        outSkus.setTimeOut(timeOut);
        outSkus.setPayPrice(payPrice);
        this.outSkus = outSkus;
    }

    public void setOutSkus(OutSkus outSkus) {
        this.outSkus = outSkus;
    }

    public OutSkus getOutSkus() {
        return outSkus;
    }

    public void setOpenDoorPwd(String openDoorPwd) {
        this.openDoorPwd = openDoorPwd;
    }

    public String getOpenDoorPwd() {
        return openDoorPwd;
    }

    public String getSimCode() {
        return simCode;
    }

    public VmStatus getStatus() {
        return status;
    }

    public void setStatus(VmStatus status) {
        this.status = status;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setSimCode(String simCode) {
        this.simCode = simCode;
    }

    public Boolean isDoorStatus() {
        return doorStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AreaRelation AreaRelationInstance() {
        if (null == areaRelation) areaRelation = new AreaRelation();
        return areaRelation;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setTempBoxCount(Integer tempBoxCount) {
        this.tempBoxCount = tempBoxCount;
    }

    public void setVmBaseInfo(String nodeName, String nodeAddress, Integer printer, Integer themeFormcat, Integer reserve, String[] supportedPayTypes, Integer vmType) {
        this.vmBaseInfo = new VmBaseInfo();
        vmBaseInfo.setNodeName(nodeName);
        vmBaseInfo.setNodeAddress(nodeAddress);
        vmBaseInfo.setPrinter(printer);
        vmBaseInfo.setThemeFormat(themeFormcat);
        vmBaseInfo.setReserve(reserve);
        vmBaseInfo.setSupportedPayTypes(supportedPayTypes);
        vmBaseInfo.setVmType(vmType);
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setChannelCfgs(List<ChannelCfg> channelCfgs) {
        this.channelCfgs = channelCfgs;
    }

    public void setSpCfgs(List<SpCfg> spCfgs) {
        this.spCfgs = spCfgs;
    }

    public void setSkuPrices(List<SkuPrice> skuPrices) {
        this.skuPrices = skuPrices;
    }

    public void setMtEnergys(List<MtEnergy> mtEnergys) {
        this.mtEnergys = mtEnergys;
    }

    public List<Supply> getSupply() {
        return supply;
    }

    public void setSupply(List<Supply> supply) {
        this.supply = supply;
    }

    public void setAreaRelation(AreaRelation areaRelation) {
        this.areaRelation = areaRelation;
    }

    public AreaRelation getAreaRelation() {
        return areaRelation;
    }

    public VmBaseInfo getVmBaseInfo() {
        return vmBaseInfo;
    }

    public void setVmBaseInfo(VmBaseInfo vmBaseInfo) {
        this.vmBaseInfo = vmBaseInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Boolean getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(Boolean doorStatus) {
        this.doorStatus = doorStatus;
    }

    public List<SkuOrder> getSkuOrder() {
        return skuOrder;
    }

    public void setSkuOrder(List<SkuOrder> skuOrder) {
        this.skuOrder = skuOrder;
    }

    public List<Portalcfg> getPortalcfgs() {
        return portalcfgs;
    }

    public void setPortalcfgs(List<Portalcfg> portalcfgs) {
        this.portalcfgs = portalcfgs;
    }

    public void addPortalcfgs(Portalcfg portalcfg) {/*
        if (null == this.portalcfgs)
            this.portalcfgs = new ArrayList<>();*/
        this.portalcfgs.add(portalcfg);
    }

    public void initPortalcfgs() {
        if (null == this.portalcfgs)
            this.portalcfgs = new ArrayList<>();
    }

    public List<Extention> getExtentionList() {
        return extentionList;
    }

    public Data setExtentionList(List<Extention> extentionList) {
        this.extentionList = extentionList;
        return this;
    }

    public List<Extentions> getExtentions() {
        return extentions;
    }

    public void setExtentions(List<Extentions> extentions) {
        this.extentions = extentions;
    }

    public void addExtentions(Extentions extention) {
        if (null == this.extentions)
            this.extentions = new ArrayList<>();
        this.extentions.add(extention);
    }
}
