package com.kdx.vms.model.custom;

import com.kdx.vms.commons.util.excel.ExcelField;

/**
 * Created by guoyang on 2017/11/24.
 */
public class StockExport {
    private String innerCode;
    private String nodeName;
    private String skuName;
    private String specification;
    private String channelCode;
    private Integer curCount;
    private Integer maxCount;
    private Integer dayCount;
    @ExcelField(title = "售货机编号", align = 2, sort = 1)
    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    @ExcelField(title = "点位", align = 2, sort = 2)
    public String getNodeName() {
        return nodeName;
    }
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    @ExcelField(title = "商品名称", align = 2, sort = 3)
    public String getSkuName() {
        return skuName;
    }
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
    @ExcelField(title = "规格", align = 2, sort = 4)
    public String getSpecification() {
        return specification;
    }
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    @ExcelField(title = "货道", align = 2, sort = 5)
    public String getChannelCode() {
        return channelCode;
    }
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
    @ExcelField(title = "库存量", align = 2, sort = 6)
    public Integer getCurCount() {
        return curCount;
    }
    public void setCurCount(Integer curCount) {
        this.curCount = curCount;
    }
    @ExcelField(title = "满仓量", align = 2, sort = 7)
    public Integer getMaxCount() {
        return maxCount;
    }
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }
    @ExcelField(title = "剩余天数", align = 2, sort = 8)
    public Integer getDayCount() {
        return dayCount;
    }
    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }
}
