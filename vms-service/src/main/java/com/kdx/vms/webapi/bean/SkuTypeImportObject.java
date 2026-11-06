package com.kdx.vms.webapi.bean;

import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Created by yangshuxi on 2017/1/17.
 */
public class SkuTypeImportObject {
    private String parentId;
    private String typeId;
    private String typeName;
    private String classCode;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void createMtSkuType(ObjectContext context, TMtSkuType skuType,TMtSkuType parent){
        if(null==skuType){
            skuType=context.newObject(TMtSkuType.class);
            skuType.setCtor(UserUtils.getErpSystem().getId());
            skuType.setCtime(new Date());
        }
        else{
            skuType.setUtor(UserUtils.getErpSystem().getId());
            skuType.setUtime(new Date());
        }
        skuType.setExternalId(typeId);
        skuType.setParent(parent);
        skuType.setTypeName(typeName);
    }
}
