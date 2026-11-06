package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IShortcutsService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 16-11-29.
 */
@Service("shortcutsService")
public class ShortcutsServiceImpl implements IShortcutsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource private StVmStrategyDao stVmStrategyDao;

    @Override
    public List getShortcutsStrategy(TMtCompany company, String strategyName, Integer modelId, Integer pageSize) {
        logger.info("company:" + company + ", strategyName:" + strategyName + ", modelId:" + modelId + ", pageSize:" + pageSize);
        if (null == company) return null;
        try {
            ObjectContext context = company.getObjectContext();
            return stVmStrategyDao.findShortcutsStrategy(context, company, strategyName, modelId, pageSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public SystemCode addShortcutsStrategy(String strategyName, String strategyDesc, Integer modelId, Integer[] skuIds, TSysUser currUser,Integer[] bindTypes,Integer[] resourceIds) {
        logger.info("strategyName:" + strategyName + ", strategyDesc" + strategyDesc + ",  modelId:" + modelId + ", skuIds:" + Arrays.toString(skuIds));
        if (StringUtil.isEmpty(strategyName)  || (null == resourceIds && null == skuIds)||bindTypes==null) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = currUser.getObjectContext();
        try {
            Date date = new Date();
            TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
            stVmStrategy.setCompany(currUser.getCompany());
            stVmStrategy.setVmModel(Cayenne.objectForPK(context, TMtVmModel.class, modelId));
            stVmStrategy.setCtime(date);
            stVmStrategy.setCtor(currUser.getId());
            stVmStrategy.setStrategyName(strategyName);
            stVmStrategy.setStrategyDesc(strategyDesc);
            stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_SHORTCUTS);
            Integer shortcutsCode = 0;
            for(Integer bindType : bindTypes){
                if(null != bindType && bindType == TMtVmShortcuts.BIND_TYPE_SKU){
                    TStVmShortcuts stVmShortcuts = context.newObject(TStVmShortcuts.class);
                    stVmShortcuts.setStrategy(stVmStrategy);
                    stVmShortcuts.setCtime(date);
                    stVmShortcuts.setCtor(currUser.getId());
                    stVmShortcuts.setSkuId(skuIds[shortcutsCode]);
                    stVmShortcuts.setShortcutsCode(++shortcutsCode + "");
                    stVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                }else if(null != bindType && bindType == TMtVmShortcuts.BIND_TYPE_URL){
                    TStVmShortcuts stVmShortcuts = context.newObject(TStVmShortcuts.class);
                    stVmShortcuts.setStrategy(stVmStrategy);
                    stVmShortcuts.setCtime(date);
                    stVmShortcuts.setCtor(currUser.getId());
                    stVmShortcuts.setResource(Cayenne.objectForPK(context,TMtVmResource.class,resourceIds[shortcutsCode]));
                    stVmShortcuts.setShortcutsCode(++shortcutsCode + "");
                    stVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                }else{
                    TStVmShortcuts stVmShortcuts = context.newObject(TStVmShortcuts.class);
                    stVmShortcuts.setStrategy(stVmStrategy);
                    stVmShortcuts.setCtime(date);
                    stVmShortcuts.setCtor(currUser.getId());
                    stVmShortcuts.setShortcutsCode(++shortcutsCode + "");
                 }
            }
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    @Override
    public SystemCode editShortcutsStrategy(Integer strategyId, String strategyName, String strategyDesc, Integer[] skuIds, TSysUser currUser,Integer[] bindTypes,Integer[] resourceIds) {
        logger.info("strategyId:" + strategyId + ", strategyName:" + strategyName + ", strategyDesc" + strategyDesc + ", skuIds:" + Arrays.toString(skuIds));
        if (null == strategyId || strategyId <= 0 || StringUtil.isEmpty(strategyName) || null == skuIds || skuIds.length < 1) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            Date date = new Date();
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
            if (null == stVmStrategy) return SystemCode.OBJECT_IS_NULL;
            stVmStrategy.setCompany(currUser.getCompany());
            stVmStrategy.setUtime(date);
            stVmStrategy.setUtor(currUser.getId());
            stVmStrategy.setStrategyName(strategyName);
            stVmStrategy.setStrategyDesc(strategyDesc);
            stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_SHORTCUTS);
            Map<String, TStVmShortcuts> stVmShortcutsMap = stVmStrategy.getStShortcuts();
            Integer shortcutsCode = 0;
            for (Integer bindType : bindTypes){
                String code = ++shortcutsCode + "";
                TStVmShortcuts stVmShortcuts = stVmShortcutsMap.get(code);
                if(null != bindType && bindType == TMtVmShortcuts.BIND_TYPE_SKU){
                    stVmShortcuts.setStrategy(stVmStrategy);
                    stVmShortcuts.setCtime(date);
                    stVmShortcuts.setCtor(currUser.getId());
                    stVmShortcuts.setSkuId(skuIds[shortcutsCode-1]);
                    stVmShortcuts.setShortcutsCode(shortcutsCode + "");
                    stVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                    stVmShortcuts.setResource(null);
                }else if(null != bindType && bindType == TMtVmShortcuts.BIND_TYPE_URL){
                    stVmShortcuts.setStrategy(stVmStrategy);
                    stVmShortcuts.setCtime(date);
                    stVmShortcuts.setCtor(currUser.getId());
                    stVmShortcuts.setResource(Cayenne.objectForPK(context,TMtVmResource.class,resourceIds[shortcutsCode-1]));
                    stVmShortcuts.setShortcutsCode(shortcutsCode + "");
                    stVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                    stVmShortcuts.setSkuId(null);
                }
            }
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    @Override
    public SystemCode deleteShortcutsStrategy(Integer strategyId, TSysUser currUser) {
        logger.info("strategyId:" + strategyId + ", currUser:" + currUser);
        if (null == strategyId || strategyId <= 0 || null == currUser) return SystemCode.PARAMETER_ERROR;
        try {
            ObjectContext context = ObjectContextFactory.getInstance().getChildObjectContext(currUser.getObjectContext());
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
            context.deleteObjects(stVmStrategy);
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    @Override
    public SystemCode deleteShortcutsStrategy(Integer[] strategyIds, TSysUser currUser) {
        logger.info("strategyIds:" + Arrays.toString(strategyIds) + ", currUser:" + currUser);
        if (null == strategyIds|| strategyIds.length == 0 || null == currUser) return SystemCode.PARAMETER_ERROR;
        try {
            ObjectContext context = ObjectContextFactory.getInstance().getChildObjectContext(currUser.getObjectContext());
            for (Integer strategyId : strategyIds) {
                TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
                context.deleteObjects(stVmStrategy);
            }
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }
}
