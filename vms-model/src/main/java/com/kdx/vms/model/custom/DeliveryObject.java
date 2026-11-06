package com.kdx.vms.model.custom;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by yangshuxi on 2017/11/20.
 */
public class DeliveryObject implements Comparable<DeliveryObject> {
    private static Logger logger = LoggerFactory.getLogger(DeliveryObject.class);
    private Integer channelId;//货道Id
    private String channelCode;//货道编号
    private Integer type;//1补2清3调
    private Integer fullCount;//满量
    private Integer clearCount;//清量(展示时是应清量,提交时是实清量)
    private Integer fillCount;//展示时是实补，不提交，后台计算
    private Integer marginCount;//补后
    private String manufactureDate;//生产日期
    private String batchNum;//批号
    private Integer termInfo = 0;//1需要填生产日期和批号,其他或者为空则不需要填
    private String skuName;//商品名称含单位
    private String skuImg;//商品图片地址
    private Integer skuId;//商品Id
    private Integer targetNum;//补后最大可调整数字
    private Integer currCount;
    private Integer skuType;//商品类型0为即售1为预定

    public static DeliveryObject ofFinish(TTaskVmChannel taskVmChannel) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(taskVmChannel.getChannel().getId())
                .setChannelCode(taskVmChannel.getChannel().getChannelCode())
                .setType(taskVmChannel.getTaskType())
                .setFullCount(taskVmChannel.getChannel().getMaxNum())
                .setMarginCount(taskVmChannel.getAfterDeliveryStock())
                .setSkuName(taskVmChannel.getSku().getSkuName() + taskVmChannel.getSku().getSkuUnit())
                .setTargetNum(taskVmChannel.getMaxCount())
                .setSkuId(getChannelSkuId(taskVmChannel))
                .setSkuType(getChannelSkuType(taskVmChannel))
                .setSkuImg(taskVmChannel.getSku().getImgUrl())
                .setClearCount(taskVmChannel.getRealClear())
                .setFillCount(taskVmChannel.getRealCount());
        return deliveryObject;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public DeliveryObject setChannelId(Integer channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public DeliveryObject setChannelCode(String channelCode) {
        this.channelCode = channelCode;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public DeliveryObject setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getFullCount() {
        return fullCount;
    }

    public DeliveryObject setFullCount(Integer fullCount) {
        this.fullCount = fullCount;
        return this;
    }

    public Integer getClearCount() {
        return clearCount;
    }

    public DeliveryObject setClearCount(Integer clearCount) {
        this.clearCount = clearCount;
        return this;
    }

    public Integer getFillCount() {
        return null == fillCount ? 0 : fillCount;
    }

    public DeliveryObject setFillCount(Integer fillCount) {
        this.fillCount = fillCount;
        return this;
    }

    public Integer getMarginCount() {
        return marginCount;
    }

    public DeliveryObject setMarginCount(Integer marginCount) {
        this.marginCount = marginCount;
        return this;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public DeliveryObject setManufactureDate(String manufactureDate) {
        if (!StringUtil.isEmpty(manufactureDate)) {
            String date = manufactureDate.trim();
            if (date.length() != 8) {
                throw new IllegalArgumentException("生产日期格式不合法");
            } else {
                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String day = date.substring(6);
                this.manufactureDate = year + "-" + month + "-" + day;
            }
        }
        return this;
    }

    public DeliveryObject fillManufactureDate(String manufactureDate) {
        if (!StringUtil.isEmpty(manufactureDate)) {
            String date = manufactureDate.trim();
            if (date.length() != 8) {
                throw new IllegalArgumentException("生产日期格式不合法");
            }
            this.manufactureDate = manufactureDate;
        }
        return this;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public DeliveryObject setBatchNum(String batchNum) {
        this.batchNum = batchNum;
        return this;
    }

    public Integer getTermInfo() {
        return termInfo;
    }

    public DeliveryObject setTermInfo(Integer termInfo) {
        this.termInfo = termInfo;
        return this;
    }

    public String getSkuName() {
        return skuName;
    }

    public DeliveryObject setSkuName(String skuName) {
        this.skuName = skuName;
        return this;
    }

    public String getSkuImg() {
        return skuImg;
    }

    public DeliveryObject setSkuImg(String skuImg) {
        this.skuImg = skuImg;
        return this;
    }

    public Integer getTargetNum() {
        return targetNum;
    }

    public DeliveryObject setTargetNum(Integer targetNum) {
        this.targetNum = targetNum;
        return this;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public DeliveryObject setSkuId(Integer skuId) {
        this.skuId = skuId;
        return this;
    }

    public Integer getCurrCount() {
        return currCount;
    }

    public DeliveryObject setCurrCount(Integer currCount) {
        this.currCount = currCount;
        return this;
    }

    public Integer getSkuType() {
        return skuType;
    }

    public DeliveryObject setSkuType(Integer skuType) {
        this.skuType = skuType;
        return this;
    }

    @Override
    public int compareTo(DeliveryObject deliveryObject) {
        int colNo1 = StringUtil.getIntValue(channelCode.split("-")[0], 0);
        int colNo2 = StringUtil.getIntValue(deliveryObject.channelCode.split("-")[0], 0);
        int rowNo1 = StringUtil.getIntValue(channelCode.split("-")[1], 0);
        int rowNo2 = StringUtil.getIntValue(deliveryObject.channelCode.split("-")[1], 0);
        return colNo1 == colNo2 ? rowNo1 - rowNo2 : colNo1 - colNo2;
    }

    public DeliveryObject calculateFillCount() {
        if (this.clearCount > this.currCount) {
            this.currCount = this.clearCount;
        }
        Integer fillCount = this.marginCount - this.currCount + this.clearCount;
        if (fillCount < 0) fillCount = 0;
        return this.setFillCount(fillCount);
    }


    //5.1 手动
    public static List<DeliveryObject> ofManualChange(TTaskVmChannel taskVmChannel, TMtVmChannelNext channelNext) {
        List<DeliveryObject> list = new ArrayList<>();
        //新关联
        if (!Objects.equals(getChannelSkuId(taskVmChannel.getChannel()), getChannelSkuId(channelNext))) {
            //新商品
            list.add(createFillManual(taskVmChannel, channelNext));
            if (null != taskVmChannel.getChannel().getSku() && taskVmChannel.getChannel().getCurr() > 0) {
                list.add(createClear(taskVmChannel.getChannel()));
            }
            return list;
        }
        //不变
        list.add(createAdjustManual(taskVmChannel, channelNext));
        return list;
    }

    public static List<DeliveryObject> ofManualWithoutChange(TTaskVmChannel taskVmChannel) {
        List<DeliveryObject> list = new ArrayList<>();
        list.add(createAdjustManual(taskVmChannel));
        return list;
    }


    //5.1 补满

    /**
     * 没有即将应用的货道
     *
     * @param channel
     * @param map
     * @return
     */
    public static List<DeliveryObject> ofFullWithoutChange(TMtVmChannel channel, Map<TMtSku, TReserveStatistics> map, boolean isFirst) {
        List<DeliveryObject> list = new ArrayList<>();
        //当前为空
        if (null == channel.getSku()) {
            return null;
        }
        //非空
        TMtSku sku = channel.getSku();
        //即售商品
        if (!Objects.equals(sku.getReserve(), TMtSku.RESERVE_TRUE)) {
            list.add(createAdjustImmediate(channel));
            return list;
        }
        //预订商品
        //有余量,且是第一次
        if (channel.getCurr() > 0 && isFirst) {
            list.add(createClear(channel));
        }
        list.add(createFillReserveAdjust(channel, map, isFirst));
        return list;
    }

    /**
     * 有即将应用的货道
     *
     * @param channel
     * @param channelNext
     * @param map
     * @return
     */
    public static List<DeliveryObject> ofFullChange(TMtVmChannel channel, TMtVmChannelNext channelNext, Map<TMtSku, TReserveStatistics> map, boolean isFirst) {
        List<DeliveryObject> list = new ArrayList<>();
        //当前为空
        if (null == channelNext.getSku()) {
            //一直空或有配置商品但已无库存或上报售空
            if (null == channel.getSku() || channel.getCurr() == 0) {
                return null;
            }
            //以前配置了商品且有余量，建请货单，清除余量
            else {
                list.add(createClear(channel));
                return list;
            }
        }
        //非空
        TMtSku sku = channelNext.getSku();
        //即售商品
        if (!Objects.equals(sku.getReserve(), TMtSku.RESERVE_TRUE)) {
            //新关联
            if (!Objects.equals(getChannelSkuId(channel), getChannelSkuId(channelNext))) {
                //新商品
                list.add(createFillImmediate(channelNext, channel));
                //旧商品
                if (null != channel.getSku() && channel.getCurr() > 0) {
                    list.add(createClear(channel));
                }
                return list;
            }
            //不变
            list.add(createAdjustImmediate(channel, channelNext));
            return list;
        }
        //预订商品
        //新关联
        if (!Objects.equals(getChannelSkuId(channel), getChannelSkuId(channelNext))) {
            //新商品
            list.add(createFillReserve(channelNext, channel, map));
            //旧商品
            if (null != channel.getSku() && channel.getCurr() > 0) {
                list.add(createClear(channel));
            }
            return list;
        }
        //不变
        //有余量,且是第一次
        if (channel.getCurr() > 0 && isFirst) {
            list.add(createClear(channel));
        }
        list.add(createFillReserveAdjust(channel, channelNext, map, isFirst));
        return list;
    }

    /**
     * 预订，货道变更
     *
     * @param channelNext
     * @param channel
     * @param map
     * @return
     */
    private static DeliveryObject createFillReserve(TMtVmChannelNext channelNext, TMtVmChannel channel, Map<TMtSku, TReserveStatistics> map) {
        TReserveStatistics reserveStatistics = map.get(channelNext.getSku());
        Integer fillCount = channelNext.getMaxNum();
        Integer targetCount = channelNext.getMaxNum();
        if (reserveStatistics != null) {
            Integer pending = reserveStatistics.getPending();
            if (fillCount >= pending) {
                fillCount = pending;
                pending = 0;
                targetCount = fillCount;
            } else {
                pending = pending - fillCount;
            }
            reserveStatistics.setPendingNum(pending);
        } else {
            fillCount = 0;
            targetCount = 0;
        }
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setType(TTaskVmChannel.CHANNEL_TYPE_FILL)
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(fillCount)
                .setSkuName(channelNext.getSku().getSkuName() + channelNext.getSku().getSkuUnit())
                .setTargetNum(targetCount)
                .setSkuId(getChannelSkuId(channelNext))
                .setSkuType(getChannelSkuType(channelNext))
                .setSkuImg(channelNext.getSku().getImgUrl())
                .setCurrCount(0)
                .setFillCount(fillCount)
                .setClearCount(0)
        ;
        if (Optional.of(channelNext)
                .map(TMtVmChannelNext::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
        }
        return deliveryObject;
    }

    /**
     * 预订，货道未更改，类型不一定是调量
     *
     * @param channel
     * @param map
     * @param isClean
     * @return
     */
    private static DeliveryObject createFillReserveAdjust(TMtVmChannel channel, Map<TMtSku, TReserveStatistics> map, boolean isClean) {
        TReserveStatistics reserveStatistics = map.get(channel.getSku());
        Integer curr = channel.getCurr();
        if (isClean) {
            curr = 0;
        }
        Integer fillCount = channel.getMaxNum() - curr;
        Integer targetCount = channel.getMaxNum();

        if (reserveStatistics != null && fillCount > 0) {
            Integer pending = reserveStatistics.getPending();
            if (fillCount >= pending) {
                fillCount = pending;
                pending = 0;
                targetCount = fillCount + curr;
            } else {
                pending = pending - fillCount;
            }
            reserveStatistics.setPendingNum(pending);
        } else {
            fillCount = 0;
            targetCount = 0;
        }
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setFullCount(channel.getMaxNum())
                .setMarginCount(targetCount)
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(targetCount)
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(curr)
                .setFillCount(fillCount)
                .setClearCount(curr > channel.getMaxNum() ? curr - channel.getMaxNum() : 0)
        ;
        if (Objects.equals(curr, channel.getMaxNum()) || (deliveryObject.getFillCount() == 0 && deliveryObject.getClearCount() == 0)) {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
        } else {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_FILL);
        }
        if (Optional.of(channel)
                .map(TMtVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getBatchNo())) {
                deliveryObject.setBatchNum(channel.getBatchNo());
            }
            if (CheckUtils.check(channel.getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 预订，货道未更改，类型不一定是调量
     *
     * @param channel
     * @param map
     * @param isClean
     * @return
     */
    private static DeliveryObject createFillReserveAdjust(TMtVmChannel channel, TMtVmChannelNext channelNext, Map<TMtSku, TReserveStatistics> map, boolean isClean) {
        TReserveStatistics reserveStatistics = map.get(channel.getSku());
        Integer curr = channel.getCurr();
        if (isClean) {
            curr = 0;
        }
        Integer fillCount = channelNext.getMaxNum() - curr;
        Integer targetCount = channelNext.getMaxNum();

        if (reserveStatistics != null && fillCount > 0) {
            Integer pending = reserveStatistics.getPending();
            if (fillCount >= pending) {
                fillCount = pending;
                pending = 0;
                targetCount = fillCount + curr;
            } else {
                pending = pending - fillCount;
            }
            reserveStatistics.setPendingNum(pending);
        } else {
            fillCount = 0;
            targetCount = 0;
        }
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(targetCount)
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(targetCount)
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(curr)
                .setFillCount(fillCount)
                .setClearCount(curr > channelNext.getMaxNum() ? curr - channelNext.getMaxNum() : 0)
        ;
        if (Objects.equals(curr, channelNext.getMaxNum()) || (deliveryObject.getFillCount() == 0 && deliveryObject.getClearCount() == 0)) {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
        } else {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_FILL);
        }
        if (Optional.of(channel)
                .map(TMtVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getBatchNo())) {
                deliveryObject.setBatchNum(channel.getBatchNo());
            }
            if (CheckUtils.check(channel.getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 清货
     *
     * @param channel
     * @return
     */
    private static DeliveryObject createClear(TMtVmChannel channel) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setType(TTaskVmChannel.CHANNEL_TYPE_CLEAN)
                .setFullCount(channel.getMaxNum())
                .setMarginCount(0)
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(0)
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(channel.getCurr())
                .setFillCount(0)
                .setClearCount(channel.getCurr())
        ;
        return deliveryObject;
    }

    /**
     * 即售，货道变更
     *
     * @param channelNext
     * @param channel
     * @return
     */
    private static DeliveryObject createFillImmediate(TMtVmChannelNext channelNext, TMtVmChannel channel) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setType(TTaskVmChannel.CHANNEL_TYPE_FILL)
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(0)
                .setSkuName(channelNext.getSku().getSkuName() + channelNext.getSku().getSkuUnit())
                .setTargetNum(channelNext.getMaxNum())
                .setSkuId(getChannelSkuId(channelNext))
                .setSkuType(getChannelSkuType(channelNext))
                .setSkuImg(channelNext.getSku().getImgUrl())
                .setCurrCount(0)
                .setFillCount(channelNext.getMaxNum())
                .setClearCount(0)
        ;
        if (Optional.of(channelNext)
                .map(TMtVmChannelNext::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
        }
        return deliveryObject;
    }

    /**
     * 即售，货道未更改，类型不一定是调量
     *
     * @param channel
     * @return
     */
    private static DeliveryObject createAdjustImmediate(TMtVmChannel channel) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setFullCount(channel.getMaxNum())
                .setMarginCount(channel.getCurr() > channel.getMaxNum() ? channel.getMaxNum() : channel.getCurr())
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(channel.getMaxNum())
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(channel.getCurr())
                .setFillCount(channel.getCurr() < channel.getMaxNum() ? channel.getMaxNum() - channel.getCurr() : 0)
                .setClearCount(channel.getCurr() > channel.getMaxNum() ? channel.getCurr() - channel.getMaxNum() : 0)
        ;
        if (Objects.equals(channel.getCurr(), channel.getMaxNum())) {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
        } else {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_FILL);
        }
        if (Optional.of(channel)
                .map(TMtVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getBatchNo())) {
                deliveryObject.setBatchNum(channel.getBatchNo());
            }
            if (CheckUtils.check(channel.getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 即售，货道未更改，类型不一定是调量
     *
     * @param channel
     * @return
     */
    private static DeliveryObject createAdjustImmediate(TMtVmChannel channel, TMtVmChannelNext channelNext) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channel.getChannelCode())
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(channel.getCurr() > channelNext.getMaxNum() ? channelNext.getMaxNum() : channel.getCurr())
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(channelNext.getMaxNum())
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(channel.getCurr())
                .setFillCount(channel.getCurr() < channelNext.getMaxNum() ? channelNext.getMaxNum() - channel.getCurr() : 0)
                .setClearCount(channel.getCurr() > channelNext.getMaxNum() ? channel.getCurr() - channelNext.getMaxNum() : 0)
        ;
        if (Objects.equals(channel.getCurr(), channelNext.getMaxNum())) {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
        } else {
            deliveryObject.setType(TTaskVmChannel.CHANNEL_TYPE_FILL);
        }
        if (Optional.of(channel)
                .map(TMtVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getBatchNo())) {
                deliveryObject.setBatchNum(channel.getBatchNo());
            }
            if (CheckUtils.check(channel.getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 即售，手动，货道未更改，类型不一定是调量
     *
     * @param channel
     * @return
     */
    private static DeliveryObject createAdjustManual(TTaskVmChannel channel) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getChannel().getId())
                .setChannelCode(channel.getChannel().getChannelCode())
                .setFullCount(channel.getChannel().getMaxNum())
                .setMarginCount(channel.getChannel().getCurr() > channel.getChannel().getMaxNum() ? channel.getChannel().getMaxNum() : channel.getChannel().getCurr())
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(channel.getChannel().getMaxNum())
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(channel.getChannel().getCurr())
                .setFillCount(channel.getSkuCount())
                .setClearCount(channel.getClearCount())
                .setType(channel.getTaskType())
        ;
        if (Optional.of(channel)
                .map(TTaskVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getChannel().getBatchNo())) {
                deliveryObject.setBatchNum(channel.getChannel().getBatchNo());
            }
            if (CheckUtils.check(channel.getChannel().getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getChannel().getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 即售，手动，货道未更改，类型不一定是调量
     *
     * @param channel
     * @return
     */
    private static DeliveryObject createAdjustManual(TTaskVmChannel channel, TMtVmChannelNext channelNext) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getChannel().getId())
                .setChannelCode(channel.getChannel().getChannelCode())
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(channel.getChannel().getCurr() > channelNext.getMaxNum() ? channelNext.getMaxNum() : channel.getChannel().getCurr())
                .setSkuName(channel.getSku().getSkuName() + channel.getSku().getSkuUnit())
                .setTargetNum(channelNext.getMaxNum())
                .setSkuId(getChannelSkuId(channel))
                .setSkuType(getChannelSkuType(channel))
                .setSkuImg(channel.getSku().getImgUrl())
                .setCurrCount(channel.getChannel().getCurr())
                .setFillCount(channel.getSkuCount())
                .setClearCount(channel.getClearCount())
                .setType(channel.getTaskType())
        ;
        if (Optional.of(channel)
                .map(TTaskVmChannel::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
            if (CheckUtils.check(channel.getChannel().getBatchNo())) {
                deliveryObject.setBatchNum(channel.getChannel().getBatchNo());
            }
            if (CheckUtils.check(channel.getChannel().getManufactureDate())) {
                String date = DateUtils.getTime1(channel.getChannel().getManufactureDate());
                date = date.replaceAll("-", "");
                deliveryObject.fillManufactureDate(date);
            }
        }
        return deliveryObject;
    }

    /**
     * 即售，手动，货道变更
     *
     * @param channel
     * @param channelNext
     * @return
     */
    private static DeliveryObject createFillManual(TTaskVmChannel channel, TMtVmChannelNext channelNext) {
        DeliveryObject deliveryObject = new DeliveryObject();
        deliveryObject.setChannelId(channel.getId())
                .setChannelCode(channelNext.getChannelCode())
                .setType(channel.getTaskType())
                .setFullCount(channelNext.getMaxNum())
                .setMarginCount(0)
                .setSkuName(channelNext.getSku().getSkuName() + channelNext.getSku().getSkuUnit())
                .setTargetNum(channelNext.getMaxNum())
                .setSkuId(getChannelSkuId(channelNext))
                .setSkuType(getChannelSkuType(channelNext))
                .setSkuImg(channelNext.getSku().getImgUrl())
                .setCurrCount(0)
                .setFillCount(channel.getSkuCount())
                .setClearCount(channel.getClearCount())
        ;
        if (Optional.of(channelNext)
                .map(TMtVmChannelNext::getSku)
                .map(TMtSku::getValidityMonitor)
                .map(monitor -> monitor == TMtSku.VALIDITY_YES)
                .orElse(false)) {
            deliveryObject.setTermInfo(1);
        }
        return deliveryObject;
    }

    private static Integer getChannelSkuId(TMtVmChannel channel) {
        return Optional.of(channel).map(TMtVmChannel::getSku).map(TMtSku::getId).orElse(null);
    }

    private static Integer getChannelSkuId(TMtVmChannelNext channel) {
        return Optional.of(channel).map(TMtVmChannelNext::getSku).map(TMtSku::getId).orElse(null);
    }

    private static Integer getChannelSkuId(TTaskVmChannel channel) {
        return Optional.of(channel).map(TTaskVmChannel::getSku).map(TMtSku::getId).orElse(null);
    }

    private static Integer getChannelSkuType(TMtVmChannel channel) {
        return Optional.of(channel).map(TMtVmChannel::getSku).map(TMtSku::getReserve).orElse(0);
    }

    private static Integer getChannelSkuType(TMtVmChannelNext channel) {
        return Optional.of(channel).map(TMtVmChannelNext::getSku).map(TMtSku::getReserve).orElse(0);
    }

    private static Integer getChannelSkuType(TTaskVmChannel channel) {
        return Optional.of(channel).map(TTaskVmChannel::getSku).map(TMtSku::getReserve).orElse(0);
    }

}
