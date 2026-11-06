package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TMtVmChannel;
import org.apache.cayenne.Cayenne;

import java.util.Date;
import java.util.Objects;

public class TMtVmChannel extends _TMtVmChannel implements Comparable<TMtVmChannel>{

    public final static int STOCK_HAVE    = 1;
    public final static int STOCK_EMPTY   = 0;
    public final static int STOCK_UNKNOEN = -1;
    /**
     * 补货完成初始化
     */
    public final static int DISTRIBUTION_INIT = -2;



    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    public Integer getTempMaxCapacity() {

        if(IntegerUtils.isPositiveInteger(super.getAdjustMaxCapacity()))
        return super.getAdjustMaxCapacity();
        if(null!=getIsFixedCapacity()&&getIsFixedCapacity()==TStVmChannel.IS_FIXED_CAPACITY)
            return getFixedCapacity();
        return getMaxCapacity()==null?0:getMaxCapacity();
    }


    public String getHasStock() {
        Integer stock = getHasNextStock();
        if (null != stock && stock.equals(0)) {
            return "无货";
        }
        Integer curr = getCurrNum();
        if (null != curr) {
            return curr <= 0 ? "无货" : "有货";
        }
        return "";
    }

    public String getCurrMax() {
        Integer curr = getCurrNum();
        Integer max  = getTempMaxCapacity();
        return "余 " + (null == curr ? 0 : curr) + " / 满 " + (null == max ? 0 : max);
    }

    public Integer getMaxNum(){
//        Integer currNum = null != getHasNextStock() && getHasNextStock() == TMtVmChannel.STOCK_EMPTY ? 0 : null;
//        if (null == currNum) currNum = null == getCurrNum() ? 0 : getCurrNum();
//        Integer maxNum = getTempMaxCapacity();
//        if(maxNum >= currNum){
//            return maxNum;
//        }
//        return 0;
        if(IntegerUtils.isPositiveInteger(super.getAdjustMaxCapacity()))
            return super.getAdjustMaxCapacity();
        if(null!=getIsFixedCapacity()&&getIsFixedCapacity()==TStVmChannel.IS_FIXED_CAPACITY)
            return getFixedCapacity();
        return getMaxCapacity()==null?0:getMaxCapacity();
    }

    public Integer getCurr(){
        Integer currNum = null != getHasNextStock() && getHasNextStock() == TMtVmChannel.STOCK_EMPTY ? 0 : null;
        if (null == currNum) currNum = null == getCurrNum() ? 0 : getCurrNum();
        return currNum;
    }
    public Integer getSkuCount(){
        Integer currNum = null != getHasNextStock() && getHasNextStock() == TMtVmChannel.STOCK_EMPTY ? 0 : null;
        if (null == currNum) currNum = null == getCurrNum() ? 0 : getCurrNum();
        Integer maxNum = getMaxNum();
        if(null == maxNum)maxNum = 0;
        if(maxNum > currNum){
            return maxNum-currNum;
        }
        return 0;
    }

    @Override
    public int compareTo(TMtVmChannel tMtVmChannel) {
        try {
            String channelCode1 = this.getChannelCode();
            String channelCode2 = tMtVmChannel.getChannelCode();
            int colNo1 = StringUtil.getIntValue(channelCode1.split("-")[0], 0);
            int colNo2 = StringUtil.getIntValue(channelCode2.split("-")[0], 0);
            int rowNo1 = StringUtil.getIntValue(channelCode1.split("-")[1], 0);
            int rowNo2 = StringUtil.getIntValue(channelCode2.split("-")[1], 0);
            return colNo1 == colNo2 ? rowNo1 - rowNo2 : colNo1 - colNo2;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getDay() {
        Date endDate = super.getManufactureEndDate();
        if (Objects.nonNull(endDate)) {
            return "" + DateUtils.daysBetween(DateUtils.getTime1(),DateUtils.getTime1(endDate));
        } else {
            return "无";
        }
    }
}
