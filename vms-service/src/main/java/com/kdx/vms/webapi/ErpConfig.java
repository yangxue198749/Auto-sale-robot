package com.kdx.vms.webapi;

import com.kdx.vms.commons.util.PropertiesLoader;

/**
 * Created by yangshuxi on 2017/4/5.
 */
public class ErpConfig {
    private static String SUFFIX = PropertiesLoader.readPropertiesValue("setting.properties","erp_suffix");
    public static String URL_PUSH_SALE_ORDER = SUFFIX.equalsIgnoreCase("production") ? "http://xwjl.ik3cloud.com/KDQuery/api/Query/ImportVMSBill" : "http://192.168.101.170/KDQuery/api/Query/ImportVMSBill";
    public static String URL_PUSH_NODE = SUFFIX.equalsIgnoreCase("production") ? "http://xwjl.ik3cloud.com/KDQueryVms/api/Query/ImporPoint" : "http://192.168.101.170/KDQuery/api/Query/ImporPoint";
    public static String URL_PUSH_CUSTOMER = SUFFIX.equalsIgnoreCase("production") ? "http://xwjl.ik3cloud.com/KDQueryVms/api/Query/ImporCustomer" : "http://192.168.101.170/KDQuery/api/Query/ImporCustomer";
    public static String URL_SEARCH_INVENTORY = SUFFIX.equalsIgnoreCase("production") ? "http://xwjl.ik3cloud.com/KDQueryVms/api/Query/InventoryQuery" : "http://192.168.101.170/KDQuery/api/Query/InventoryQuery";
    public static String URL_PUSH_ALLOCATION = SUFFIX.equalsIgnoreCase("production") ? "http://xwjl.ik3cloud.com/KDQueryVms/api/Query/ImporAllocation" : "http://192.168.101.170/KDQuery/api/Query/ImporAllocation";
    public static String erpUrl = SUFFIX.equalsIgnoreCase("production") ? "54.222.231.221" : "192.168.101.170";
}
