package com.kdx.vms.webapi.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtWarehouse;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Created by yangshuxi on 2017/1/17.
 */
public class WarehouseImportObject {
    private String warehouseCode;
    private String warehouseName;
    private String warehouseType;
    private String warehouseManager;
    private String warehousePhone;
    private String longitude;
    private String dimensions;
    private String warehouseLv;
    private String warehouseAddr;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    public String getWarehouseManager() {
        return warehouseManager;
    }

    public void setWarehouseManager(String warehouseManager) {
        this.warehouseManager = warehouseManager;
    }

    public String getWarehousePhone() {
        return warehousePhone;
    }

    public void setWarehousePhone(String warehousePhone) {
        this.warehousePhone = warehousePhone;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getWarehouseLv() {
        return warehouseLv;
    }

    public void setWarehouseLv(String warehouseLv) {
        this.warehouseLv = warehouseLv;
    }

    public String getWarehouseAddr() {
        return warehouseAddr;
    }

    public void setWarehouseAddr(String warehouseAddr) {
        this.warehouseAddr = warehouseAddr;
    }

    public void createWarehouse(ObjectContext context, TMtWarehouse warehouse){
        if(null==warehouse){
            warehouse=context.newObject(TMtWarehouse.class);
            warehouse.setCtor(UserUtils.getErpSystem().getId());
            warehouse.setCtime(new Date());
        }
        else{
            warehouse.setUtor(UserUtils.getErpSystem().getId());
            warehouse.setUtime(new Date());
        }
        warehouse.setWarehouseCode(warehouseCode);
        warehouse.setWarehouseName(warehouseName);
        warehouse.setWarehouseType(warehouseType);
        warehouse.setWarehouseManager(warehouseManager);
        warehouse.setWarehousePhone(warehousePhone);
        warehouse.setLongitude(longitude);
        warehouse.setDimensions(dimensions);
        warehouse.setWarehouseLevel(StringUtil.getIntValue(warehouseLv,0));
        warehouse.setWarehouseAddress(warehouseAddr);
    }
}
