package com.kdx.vms.webapi.model;

//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
/**
 * Created by yangshuxi on 2016/12/16.
 */
public class CustomerObject extends ErpObject {
//    @Expose
//    @SerializedName("LIN")
//    private String lin = LIN;
//    @Expose
//    @SerializedName("OPTYPE")
//    private String optype = "1";
//    @Expose
//    @SerializedName("FNumber")
//    private String number = "";
//    @Expose
//    @SerializedName("FWHtype")
//    private String type = "";
//    @Expose
//    @SerializedName("FWHLevel")
//    private String level = "";
//    @Expose
//    @SerializedName("Flongitude")
//    private String longitude = "";
//
//    @Expose
//    @SerializedName("FName")
//    private String customerName = "";
//    @Expose
//    @SerializedName("FTAXREGISTERCODE")
//    private String taxCode = "";
//    @Expose
//    @SerializedName("FADDRESS")
//    private String customerAddress = "";
//    @Expose
//    @SerializedName("BankEntries")
//    private List<CustomerBank> customerBank = new ArrayList<>();
//    @Expose
//    @SerializedName("FcontactEntries")
//    private List<CustomerLocation> customerLocation = new ArrayList<>();
//
//    class CustomerBank{
//        @Expose
//        @SerializedName("FOPENBANKNAME")
//        private String collectionAccountType = "";
//        @Expose
//        @SerializedName("FBANKCODE")
//        private String collectionAccount = "";
//    }
//
//    class CustomerLocation{
//        @Expose
//        @SerializedName("FCONTACT")
//        private String managerName = "";
//        @Expose
//        @SerializedName("FMOBILEPHONE")
//        private String managerPhone = "";
//        @Expose
//        @SerializedName("FCONTACTEMAIL")
//        private String customerEmail = "";
//    }
//
//    public void setProperties(TMtCustomer customer,String operate){
//        optype = operate;
//        customerName = customer.getCustomerName();
//        number = customer.getId()+"";
//        taxCode = customer.getTaxCode();
//        customerAddress = customer.getCustomerAddress();
//        CustomerBank cb = new CustomerBank();
//        cb.collectionAccount = customer.getCollectionAccount();
//        cb.collectionAccountType = customer.getCollectionAccountType();
//        customerBank.add(cb);
//        CustomerLocation cl = new CustomerLocation();
//        cl.customerEmail = customer.getCustomerEmail();
//        cl.managerName = customer.getManagerName();
//        cl.managerPhone = customer.getManagerPhone();
//        customerLocation.add(cl);
//    }
//
//    private CustomerObject() {
//    }
//
//    public static CustomerObject getInstance(TMtCustomer customer,String operate){
//        CustomerObject customerObject = new CustomerObject();
//        customerObject.setProperties(customer,operate);
//        return customerObject;
//    }
}
