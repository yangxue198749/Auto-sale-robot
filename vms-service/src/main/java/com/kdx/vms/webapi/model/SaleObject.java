package com.kdx.vms.webapi.model;

//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
/**
 * Created by yangshuxi on 2017/1/19.
 */
public class SaleObject extends ErpObject {
//    @Expose
//    @SerializedName("LIN")
//    private String lin = LIN;
//    @Expose
//    @SerializedName("OPTYPE")
//    private Integer optype = 1;
//    @Expose
//    @SerializedName("FQty")
//    private Integer qty = 1;
//    @Expose
//    @SerializedName("FModel")
//    private String model = "";
//
//    @Expose
//    @SerializedName("FItemNo")
//    private String skuCode = "";
//    @Expose
//    @SerializedName("FItemName")
//    private String skuName = "";
//    @Expose
//    @SerializedName("FSaleID")
//    private Long saleId;
//    @Expose
//    @SerializedName("FSaleNo")
//    private String saleSeq = "";
//    @Expose
//    @SerializedName("FMachineID")
//    private Integer vmId;
//    @Expose
//    @SerializedName("FMachineNo")
//    private String innerCode = "";
//    @Expose
//    @SerializedName("FHDID")
//    private String channelId = "";
//    @Expose
//    @SerializedName("FPaymentTypeNo")
//    private Integer payType;
//    @Expose
//    @SerializedName("FPaymentType")
//    private String payTypeName = "";
//    @Expose
//    @SerializedName("FAmount")
//    private String payAmount = "";
//    @Expose
//    @SerializedName("FPayTime")
//    private String costTime = "";
//    @Expose
//    @SerializedName("FDate")
//    private String costDate = "";
//    @Expose
//    @SerializedName("FPayStateNo")
//    private Integer costStatus;
//    @Expose
//    @SerializedName("FPayState")
//    private String costStatusName = "";
//    @Expose
//    @SerializedName("FOutTime")
//    private String outTime = "";
//    @Expose
//    @SerializedName("FOutStateNo")
//    private Integer outStatus;
//    @Expose
//    @SerializedName("OutState")
//    private String outStatusName = "";
//    @Expose
//    @SerializedName("FFailReason")
//    private String failReason = "";
//    @Expose
//    @SerializedName("FStockNo")
//    private String wareHouseId = "";
//    @Expose
//    @SerializedName("FStockName")
//    private String wareHouseName = "";
//
////    @Expose
////    @SerializedName("")
//    private String areaName = "";
////    @Expose
////    @SerializedName("")
//    private String customerName = "";
//
//
//    public void setProperties(TSlOrderMaster TSlOrderMaster){
//        skuCode = TSlOrderMaster.getSku().getSkuCode();
//        skuName = TSlOrderMaster.getSku().getSkuName();
//        saleId = TSlOrderMaster.getId();
//        saleSeq = TSlOrderMaster.getSaleSeq();
//        vmId = TSlOrderMaster.getMtVm().getId();
//        innerCode = TSlOrderMaster.getMtVm().getInnerCode();
//        if(null!=TSlOrderMaster.getVendout().getChannel()) {
//            channelId = TSlOrderMaster.getVendout().getChannel().getChannelCode();
//        }
//        payType = TSlOrderMaster.getCost().getCostType();
//        payTypeName = TSlOrderMaster.getPayTypeName();
//        payAmount = convert(TSlOrderMaster.getCost().getCostPrice());
//        costTime = DateUtils.getTime2(TSlOrderMaster.getCostTime());
//        costDate = DateUtils.getTime1(TSlOrderMaster.getCostTime());
//        costStatus = TSlOrderMaster.getCost().getCostStatus();
//        costStatusName = TSlOrderMaster.getCostStatusName();
//        outTime = DateUtils.getTime2(TSlOrderMaster.getOutTime());
//        outStatus = TSlOrderMaster.getVendout().getVendoutStatus();
//        outStatusName = TSlOrderMaster.getOutStatusName();
//        failReason = TSlOrderMaster.getFailReason();
//        TMtArea mtArea= AreaCache.getArea(TSlOrderMaster.getMtVm().getNode().getAreaId());
//        if(null!=mtArea.getWareHouseArea()&&!mtArea.getWareHouseArea().isEmpty()){
//            wareHouseId = mtArea.getWareHouseArea().get(0).getWareHouse().getId()+"";
//            wareHouseName = mtArea.getWareHouseArea().get(0).getWareHouse().getWarehouseName();
//        }
//        Integer provinceId=TSlOrderMaster.getMtVm().getNode().getProvinceId();
//        Integer cityId=TSlOrderMaster.getMtVm().getNode().getCityId();
//        Integer areaId=TSlOrderMaster.getMtVm().getNode().getAreaId();
//        String provinceDesc=AreaCache.getAreaName(provinceId);
//        String cityDesc=AreaCache.getAreaName(cityId);
//        String areaDesc=AreaCache.getAreaName(areaId);
//        areaName = provinceDesc+"-"+cityDesc+"-"+areaDesc;
//        customerName = TSlOrderMaster.getMtVm().getCompany().getCustomer().getCustomerName();
//    }
//
//    public static SaleObject getInstance(TSlOrderMaster slOrderSale){
//        SaleObject saleObject = new SaleObject();
//        saleObject.setProperties(slOrderSale);
//        return saleObject;
//    }
}
