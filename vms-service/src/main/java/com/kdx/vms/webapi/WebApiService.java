package com.kdx.vms.webapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.model.bean.TMtWarehouse;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtSkuDao;
import com.kdx.vms.orm.dao.MtSkuTypeDao;
import com.kdx.vms.orm.dao.MtWarehouseDao;
import com.kdx.vms.webapi.bean.ReturnErrorCode;
import com.kdx.vms.webapi.bean.SkuImportObject;
import com.kdx.vms.webapi.bean.SkuTypeImportObject;
import com.kdx.vms.webapi.bean.WarehouseImportObject;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yangshuxi on 2016/12/30.
 */
@Service
public class WebApiService {
    @Autowired
    private MtSkuDao mtSkuDao;
    @Autowired
    private MtSkuTypeDao mtSkuTypeDao;
    @Autowired
    private MtWarehouseDao mtWarehouseDao;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public ReturnErrorCode skuInfo(JSONArray jsonArray, String operation){
        switch (operation){
            case "A":
                return updateOrAddSku(jsonArray);
            case "U":
                return updateOrAddSku(jsonArray);
            case "D":
                return ReturnErrorCode.DELETE_INVALIDATE;
            default:
                return ReturnErrorCode.OPERATION_INVALIDATE;
        }
    }

    private ReturnErrorCode updateOrAddSku(JSONArray jsonArray){
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String json = jsonObject.toString();
                SkuImportObject sku = JSON.parseObject(json,SkuImportObject.class);
                String skuCode = sku.getSkuCode();
                if(StringUtil.isEmpty(skuCode)){
                    return ReturnErrorCode.NO_SKU_CODE;
                }
                if(StringUtil.isEmpty(sku.getSkuTypeId())){
                    return ReturnErrorCode.NO_SKU_TYPE_ID;
                }
                if(skuCode.charAt(0)!='1'||skuCode.charAt(1)!='4'){
                    return ReturnErrorCode.INVALIDATE_SKU_CODE;
                }
                TMtSku mtSku=mtSkuDao.findByCode(context,skuCode);
                String typeId = sku.getSkuTypeId();
                TMtSkuType skuType = mtSkuTypeDao.findByExternalId(context,typeId);
                if(null==skuType){
                    return ReturnErrorCode.NO_SKU_TYPE;
                }
                sku.createMtSku(context,mtSku,skuType);
            }
            context.commitChanges();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage());
            return ReturnErrorCode.DATA_INVALIDATE;
        }
        return ReturnErrorCode.SUCCESS;
    }
    public ReturnErrorCode skuClassInfo(JSONArray jsonArray,String operation){
        switch (operation){
            case "A":
                return updateOrAddSkuType(jsonArray);
            case "U":
                return updateOrAddSkuType(jsonArray);
            case "D":
                return ReturnErrorCode.DELETE_INVALIDATE;
            default:
                return ReturnErrorCode.OPERATION_INVALIDATE;
        }
    }

    private ReturnErrorCode updateOrAddSkuType(JSONArray jsonArray){
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String json = jsonObject.toString();
                SkuTypeImportObject skuType = JSON.parseObject(json,SkuTypeImportObject.class);
                if(StringUtil.isEmpty(skuType.getTypeId())){
                    return ReturnErrorCode.NO_SKU_TYPE_ID;
                }
                String classCode = skuType.getClassCode();
                if(StringUtil.isEmpty(classCode)){
                    return ReturnErrorCode.NO_TYPE_CODE;
                }
                if(classCode.charAt(0)!='1'||classCode.charAt(1)!='4'){
                    return ReturnErrorCode.INVALIDATE_TYPE_CODE_ONE_LEVEL;
                }
                TMtSkuType parent = null;
                switch (classCode.length()){
                    case 5:
                        break;
                    case 8:
                        parent = mtSkuTypeDao.findByExternalId(context,skuType.getParentId());
                        if(null==parent)return ReturnErrorCode.NO_SKU_TYPE_PARENT;
                        break;
                    default:
                        return ReturnErrorCode.INVALIDATE_TYPE_CODE;
                }
                TMtSkuType mtSkuType = mtSkuTypeDao.findByExternalId(context,skuType.getTypeId());
                skuType.createMtSkuType(context,mtSkuType,parent);
                //逐条保存，因为可能之间有依赖关系
                context.commitChanges();
            }
//            context.commitChanges();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage());
            return ReturnErrorCode.DATA_INVALIDATE;
        }
        return ReturnErrorCode.SUCCESS;
    }
    public ReturnErrorCode warehouseInfo(JSONArray jsonArray,String operation){
        switch (operation){
            case "A":
                return updateOrAddWarehouse(jsonArray);
            case "U":
                return updateOrAddWarehouse(jsonArray);
            case "D":
                return ReturnErrorCode.DELETE_INVALIDATE;
            default:
                return ReturnErrorCode.OPERATION_INVALIDATE;
        }
    }
    private ReturnErrorCode updateOrAddWarehouse(JSONArray jsonArray){
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String json = jsonObject.toString();
                WarehouseImportObject warehouse = JSON.parseObject(json,WarehouseImportObject.class);
                if(StringUtil.isEmpty(warehouse.getWarehouseCode())){
                    return ReturnErrorCode.NO_WAREHOUSE_CODE;
                }
                TMtWarehouse mtWarehouse = mtWarehouseDao.findByCode(context,warehouse.getWarehouseCode());
                warehouse.createWarehouse(context,mtWarehouse);
                context.commitChanges();
            }
//            context.commitChanges();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage());
            return ReturnErrorCode.DATA_INVALIDATE;
        }
        return ReturnErrorCode.SUCCESS;
    }
}
