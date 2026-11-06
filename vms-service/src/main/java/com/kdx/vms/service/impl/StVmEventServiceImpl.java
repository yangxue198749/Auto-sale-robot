package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.StVmEventDao;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IStVmEventService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/1.
 */
@Service("stVmEventService")
public class StVmEventServiceImpl implements IStVmEventService{
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Autowired
    StVmStrategyDao stVmStrategyDao;
    @Autowired
    StVmEventDao stVmEventDao;
    @Autowired
    private ISysLogService sysLogService;
    @Override
    public List findVmEventList(TSysUser currentUser, ObjectContext context,String discountType,String strategyName,Integer pageSize) {
        return stVmEventDao.findVmEventStrategy(context,currentUser.getCompany(),discountType,strategyName,pageSize);
    }

    @Override
    public List findSkuEventList(TSysUser currentUser, ObjectContext context, String strategyName, Integer pageSize) {
        return stVmEventDao.findSkuEventStrategy(context,currentUser.getCompany(),strategyName,pageSize);
    }

    @Override
    public Map<String, Object> saveVmEvent(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer discountType, Integer discountValue) {
        if(StringUtil.isEmpty(strategyName)||!IntegerUtils.isPositiveInteger(discountType)||!IntegerUtils.isPositiveInteger(discountValue))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setCtime(new Date());
        stVmStrategy.setCompany(currentUser.getCompany());
        stVmStrategy.setCtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_EVENT);
        TStVmEvent stVmEvent=context.newObject(TStVmEvent.class);
        stVmEvent.setCtime(new Date());
        stVmEvent.setCtor(Cayenne.intPKForObject(currentUser));
        stVmEvent.setDiscountType(discountType);
        stVmEvent.setEventType(TStVmEvent.EVENT_TYPE_VM);
        if(discountType==1&&discountValue>100){
            context.rollbackChanges();
            return ReturnCode.DATA_OVER.toMap();
        }
        stVmEvent.setDiscountValue(discountValue);
        stVmEvent.setStrategy(stVmStrategy);
            context.commitChanges();
            sysLogService.saveSysLog("新建整机促销策略"+stVmStrategy.getStrategyId(), TSysLog.INSERT, SyslogEnum.ST_VM_EVENT,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveVmEvent(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer discountType, Integer discountValue, Integer strategyId) {
        if(StringUtil.isEmpty(strategyName)||!IntegerUtils.isPositiveInteger(discountType)||!IntegerUtils.isPositiveInteger(discountValue)||!IntegerUtils.isPositiveInteger(strategyId))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setUtime(new Date());
        stVmStrategy.setUtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setCompany(currentUser.getCompany());
        List<TStVmEvent> list=stVmStrategy.getStEvent();
        context.deleteObjects(list);
        TStVmEvent stVmEvent=context.newObject(TStVmEvent.class);
        stVmEvent.setCtime(new Date());
        stVmEvent.setCtor(Cayenne.intPKForObject(currentUser));
        stVmEvent.setDiscountType(discountType);
        stVmEvent.setEventType(TStVmEvent.EVENT_TYPE_VM);
        if(discountType==1&&discountValue>100){
            context.rollbackChanges();
            return ReturnCode.DATA_OVER.toMap();
        }
        stVmEvent.setDiscountValue(discountValue);
        stVmEvent.setStrategy(stVmStrategy);
            context.commitChanges();
            sysLogService.saveSysLog("修改整机促销策略"+strategyId, TSysLog.UPDATE, SyslogEnum.ST_VM_EVENT,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveSkuEvent(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer[] skuIds, Integer[] discountTypes, Integer[] discountValues) {
        if(null==skuIds||null==discountValues||null==discountTypes||StringUtil.isEmpty(strategyName)){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        if(skuIds.length != discountTypes.length || skuIds.length != discountValues.length){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        try {
        TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setCtime(new Date());
        stVmStrategy.setCompany(currentUser.getCompany());
        stVmStrategy.setCtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_EVENT);
        for (int i = 0; i <skuIds.length ; i++) {
            if(discountTypes[i]==1&&discountValues[i]>100){
                context.rollbackChanges();
                return ReturnCode.DATA_OVER.toMap();
            }
            TStVmEvent stVmEvent = context.newObject(TStVmEvent.class);
            stVmEvent.setCtime(new Date());
            stVmEvent.setCtor(Cayenne.intPKForObject(currentUser));
            stVmEvent.setSku(Cayenne.objectForPK(context,TMtSku.class, skuIds[i]));
            stVmEvent.setEventType(TStVmEvent.EVENT_TYPE_SKU);
            stVmEvent.setDiscountType(discountTypes[i]);
            stVmEvent.setDiscountValue(discountValues[i]);
            stVmEvent.setStrategy(stVmStrategy);
        }
            context.commitChanges();
            sysLogService.saveSysLog("新建商品促销策略"+stVmStrategy.getStrategyId(), TSysLog.INSERT, SyslogEnum.ST_VM_EVENT,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveSkuEvent(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer[] skuIds, Integer[] discountTypes, Integer[] discountValues, Integer strategyId) {
        if(null==skuIds||null==discountValues||null==discountTypes||StringUtil.isEmpty(strategyName)||!IntegerUtils.isPositiveInteger(strategyId)){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        if(skuIds.length != discountTypes.length || skuIds.length != discountValues.length){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        try {
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setUtime(new Date());
        stVmStrategy.setUtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setCompany(currentUser.getCompany());
        List<TStVmEvent> list=stVmStrategy.getStEvent();
        context.deleteObjects(list);
        for (int i = 0; i <skuIds.length ; i++) {
            if(discountTypes[i]==1&&discountValues[i]>100){
                context.rollbackChanges();
                return ReturnCode.DATA_OVER.toMap();
            }
            TStVmEvent stVmEvent = context.newObject(TStVmEvent.class);
            stVmEvent.setCtime(new Date());
            stVmEvent.setCtor(Cayenne.intPKForObject(currentUser));
            stVmEvent.setEventType(TStVmEvent.EVENT_TYPE_SKU);
            stVmEvent.setSku(Cayenne.objectForPK(context,TMtSku.class, skuIds[i]));
            stVmEvent.setDiscountType(discountTypes[i]);
            stVmEvent.setDiscountValue(discountValues[i]);
            stVmEvent.setStrategy(stVmStrategy);
        }
            context.commitChanges();
            sysLogService.saveSysLog("修改商品促销策略"+strategyId, TSysLog.UPDATE, SyslogEnum.ST_VM_EVENT,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
}
