package com.kdx.vms.webapi.model;

//import com.google.gson.annotations.SerializedName;
/**
 * Created by yangshuxi on 2016/12/16.
 */
public class NodeObject extends ErpObject{
//    @Expose
//    @SerializedName("LIN")
//    private String lin = LIN;
//    @Expose
//    @SerializedName("OPTYPE")
//    private String optype = "1";
//    @Expose
//    @SerializedName("FID")
//    private String id = "";
//    @Expose
//    @SerializedName("FNumber")
//    private String number = "";
//    @Expose
//    @SerializedName("FName")
//    private String name = "";
//    @Expose
//    @SerializedName("FAD_LEVEL")
//    private String fadLevel = "";
//    @Expose
//    @SerializedName("FVF_LEVEL")
//    private String fvfLevel = "";
//
//    @Expose
//    @SerializedName("FNODE_ID")
//    private String nodeId = "";
//    @Expose
//    @SerializedName("FNODE_NAME")
//    private String nodeName = "";
//    @Expose
//    @SerializedName("FNODE_ADDRESS")
//    private String nodeAddress = "";
//    @Expose
//    @SerializedName("FLONGITUDE")
//    private String longitude = "";
//    @Expose
//    @SerializedName("FDIMENSIONS")
//    private String dimensions = "";
//    @Expose
//    @SerializedName("FAREA_ID")
//    private String areaId = "";
////    @Expose
////    @SerializedName("FAREA_NAME")
//    private String areaName = "";
//    @Expose
//    @SerializedName("FCUSTOMER_ID")
//    private String customerId = "";
//    @Expose
//    @SerializedName("FCUSTOMER_NAME")
//    private String customerName = "";
//    @Expose
//    @SerializedName("FCUSTOMER_TYPE")
//    private String customerType = "";
//    @Expose
//    @SerializedName("FDELIVERY_MAN_ID")
//    private String deliveryId = "";
//    @Expose
//    @SerializedName("FDELIVERY_MAN_NAME")
//    private String deliveryName = "";
//    @Expose
//    @SerializedName("FREPAIR_MAN_ID")
//    private String repairId = "";
//    @Expose
//    @SerializedName("FREPAIR_MAN_NAME")
//    private String repairName = "";
//    @Expose
//    @SerializedName("FOPT_START_TIME")
//    private String optStartTime = "";
//    @Expose
//    @SerializedName("FOPT_END_TIME")
//    private String optEndTime = "";
//    @Expose
//    @SerializedName("FNODE_PLACE_ID")
//    private String businessDistrictId = "";
//    @Expose
//    @SerializedName("FNODE_PLACE_NAME")
//    private String businessDistrictName = "";
//    @Expose
//    @SerializedName("FPIC_NAME")
//    private String propertyManager = "";
//    @Expose
//    @SerializedName("FPIC_LEVEL")
//    private String propertyLevel = "";
//    @Expose
//    @SerializedName("FPIC_PHONE")
//    private String propertyPhone = "";
//
//    public void setProperties(TMtNode mtNode,ObjectContext context,String operate){
//        optype = operate;
//        number = mtNode.getId()+"";
//        name = mtNode.getNodeName();
//        id =  mtNode.getId()+"";
//        nodeId = mtNode.getId()+"";
//        nodeName = mtNode.getNodeName();
//        nodeAddress = mtNode.getNodeAddress();
//        longitude = mtNode.getLongitude();
//        dimensions = mtNode.getLatitude();
//        areaId = mtNode.getAreaId()+"";
////        areaName = AreaCache.getAreaName(mtNode.getAreaId());
//        customerId = mtNode.getCompany().getCustomer().getId()+"";
//        customerName = mtNode.getCompany().getCustomer().getCustomerName();
//        TSysDictionary sysDictionary= Cayenne.objectForPK(context,TSysDictionary.class,mtNode.getCompany().getCustomer().getCustomerType());
//        customerType = sysDictionary.getDictionaryName();
//        if(null!=mtNode.getDelivery()){
//            deliveryId = mtNode.getDelivery().getId()+"";
//            deliveryName = mtNode.getDelivery().getUserName();
//        }
//        if(null!=mtNode.getRepair()){
//            repairId = mtNode.getRepair().getId()+"";
//            repairName = mtNode.getRepair().getUserName();
//        }
//        optStartTime = mtNode.getOptStartTime();
//        optEndTime = mtNode.getOptEndTime();
//        if(IntegerUtils.isPositiveInteger(mtNode.getBusinessDistrictId())){
//            businessDistrictId = mtNode.getBusinessDistrictId()+"";
//            TMtBusinessDistrict mtBusinessDistrict= Cayenne.objectForPK(context,TMtBusinessDistrict.class,mtNode.getBusinessDistrictId());
//            businessDistrictName = mtBusinessDistrict.getBusinessDistrictName();
//        }
//        propertyManager = mtNode.getPropertyManager();
//        propertyLevel = mtNode.getPropertyLevel();
//        propertyPhone = mtNode.getPropertyPhone();
//    }
//
//    private NodeObject() {
//    }
//
//    public static NodeObject getInstance(TMtNode mtNode,ObjectContext context,String operate){
//        NodeObject nodeObject = new NodeObject();
//        nodeObject.setProperties(mtNode,context,operate);
//        return nodeObject;
//    }
}
