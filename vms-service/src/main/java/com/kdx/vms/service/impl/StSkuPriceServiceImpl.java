package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IStSkuPriceService;

import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/30.
 */
@Service("stSkuPriceService")
public class StSkuPriceServiceImpl implements IStSkuPriceService{
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    protected final static String KEY_CODE = "code";
    @Autowired
    StVmStrategyDao stVmStrategyDao;
    @Autowired
    private ISysLogService sysLogService;
    @Override
    public List findStrategyList(TSysUser currentUser, ObjectContext context, String strategyName,Integer pageSize) {
        return stVmStrategyDao.findStVmStrategy(context,currentUser.getCompany(), TStVmStrategy.TYPE_ST_VM_PRICE,strategyName,pageSize);
    }

    @Override
    public Map<String, Object> saveStrategy(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer[] skuIds, Integer[] realPrices) {
        if(null==skuIds||null==realPrices||skuIds.length != realPrices.length||StringUtil.isEmpty(strategyName))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setCtime(new Date());
        stVmStrategy.setCompany(currentUser.getCompany());
        stVmStrategy.setCtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_PRICE);
        for (int i = 0; i <skuIds.length ; i++) {
            TStVmPrice stVmPrice = context.newObject(TStVmPrice.class);
            stVmPrice.setCtime(new Date());
            stVmPrice.setCtor(Cayenne.intPKForObject(currentUser));
            stVmPrice.setSku(Cayenne.objectForPK(context,TMtSku.class, skuIds[i]));
            stVmPrice.setRealPrice(realPrices[i]);
            stVmPrice.setStrategy(stVmStrategy);
        }
            context.commitChanges();
            sysLogService.saveSysLog("新建价格策略"+stVmStrategy.getStrategyId(), TSysLog.INSERT, SyslogEnum.ST_SKU_PRICE,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveStrategy(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer[] skuIds, Integer[] realPrices, Integer strategyId) {
        if(null==skuIds||null==realPrices||skuIds.length != realPrices.length||StringUtil.isEmpty(strategyName)||!IntegerUtils.isPositiveInteger(strategyId))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        stVmStrategy.setStrategyName(strategyName);
        stVmStrategy.setStrategyDesc(strategyDesc);
        stVmStrategy.setUtime(new Date());
        stVmStrategy.setUtor(Cayenne.intPKForObject(currentUser));
        stVmStrategy.setCompany(currentUser.getCompany());
        List<TStVmPrice> list=stVmStrategy.getStPrice();
        //删除现有
        context.deleteObjects(list);
        for (int i = 0; i <skuIds.length ; i++) {
            TStVmPrice stVmPrice = context.newObject(TStVmPrice.class);
            stVmPrice.setCtime(new Date());
            stVmPrice.setCtor(Cayenne.intPKForObject(currentUser));
            stVmPrice.setSku(Cayenne.objectForPK(context,TMtSku.class, skuIds[i]));
            stVmPrice.setRealPrice(realPrices[i]);
            stVmPrice.setStrategy(stVmStrategy);
        }
            context.commitChanges();
            sysLogService.saveSysLog("修改价格策略"+strategyId, TSysLog.UPDATE, SyslogEnum.ST_SKU_PRICE,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

}
