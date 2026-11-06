package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmShortcutsService;
import com.kdx.vms.service.ISysLogService;
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
 * Created by yangshuxi on 2016/12/9.
 */
@Service
public class MtVmShortcutsServiceImpl implements IMtVmShortcutsService {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Resource
    private IMcsService mcsService;
    @Resource
    private ISysLogService sysLogService;
    @Override
    public Map<String, Object> saveShortcuts(TSysUser currentUser, ObjectContext context, Integer[] ids,Integer[] skuIds,String[] shortcutsCodes,Integer[] resourceIds,Integer[] bindTypes) {
        if(null==ids||(null==skuIds && resourceIds==null)||null==shortcutsCodes || bindTypes == null)return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        //遍历所选机器
            for (Integer id : ids) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, id);
                List<TMtVmShortcuts> list = mtVm.getShortcutsByDB();
                for (TMtVmShortcuts mtVmShortcuts : list) {
                    for (int j = 0; j < skuIds.length; j++) {
                        if (shortcutsCodes[j].equals(mtVmShortcuts.getShortcutsCode())) {
                            if (null != skuIds[j] && bindTypes[j] == TMtVmShortcuts.BIND_TYPE_SKU) {
                                mtVmShortcuts.setSku(Cayenne.objectForPK(context, TMtSku.class, skuIds[j]));
                                mtVmShortcuts.setUtor(currentUser.getId());
                                mtVmShortcuts.setUtime(new Date());
                                mtVmShortcuts.setResource(null);
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                                break;
                            }else if (null != resourceIds[j] && bindTypes[j] == TMtVmShortcuts.BIND_TYPE_URL){
                                mtVmShortcuts.setSku(null);
                                mtVmShortcuts.setUtor(currentUser.getId());
                                mtVmShortcuts.setUtime(new Date());
                                mtVmShortcuts.setResource(Cayenne.objectForPK(context, TMtVmResource.class, resourceIds[j]));
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                            }
                        }
                    }
                }
                List<TMtVmStrategy> stList = mtVm.getStrategy();
                boolean flag = false;
                for (TMtVmStrategy mtVmStrategy : stList) {
                    if (mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_SHORTCUTS)) {
                        mtVmStrategy.clear(currentUser.getId());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TMtVmStrategy mtVmStrategy = context.newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_SHORTCUTS);
                    mtVmStrategy.setMtVm(mtVm);
                    mtVmStrategy.clear(currentUser.getId());
                }
            }

            context.commitChanges();
            String mcsMsg = mcsService.pushShortCuts(context, Arrays.asList(ids),null);
            if(StringUtil.isEmpty(mcsMsg)){
                sysLogService.saveSysLog("配置闪购键到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),context);
                return ReturnCode.SUCCESS.toMap();
            }else{
                return ReturnCode.MCS_FAIL.toMap();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveShortcuts(TSysUser currentUser, ObjectContext context, Integer[] ids, Integer strategyId) {
        if(!IntegerUtils.isPositiveInteger(strategyId)){
            return ReturnCode.NO_SELECT_STRATEGY.toMap();
        }
        if(null==ids)return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
        Map<String,TStVmShortcuts> map=stVmStrategy.getStShortcuts();
        //遍历所选机器
            for (Integer id : ids) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, id);
                List<TMtVmShortcuts> list = mtVm.getShortcutsByDB();
                for (TMtVmShortcuts mtVmShortcuts : list) {
                    TStVmShortcuts stVmShortcuts =  map.get(mtVmShortcuts.getShortcutsCode());
                    if(null != stVmShortcuts ){
                        if(stVmShortcuts.getBindType() ==TMtVmShortcuts.BIND_TYPE_SKU ){
                            Integer skuId = stVmShortcuts.getSkuId();
                            if (null == skuId) {
                                mtVmShortcuts.setSku(null);
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                                mtVmShortcuts.setResource(null);
                            }else{
                                mtVmShortcuts.setSku(Cayenne.objectForPK(context, TMtSku.class, skuId));
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                                mtVmShortcuts.setResource(null);
                            }
                        }else if(stVmShortcuts.getBindType() ==TMtVmShortcuts.BIND_TYPE_URL){
                            Integer resourceId = stVmShortcuts.getResource().getId();
                            if (null == resourceId) {
                                mtVmShortcuts.setSku(null);
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                                mtVmShortcuts.setResource(null);
                            }else{
                                mtVmShortcuts.setSku(null);
                                mtVmShortcuts.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                                mtVmShortcuts.setResource(Cayenne.objectForPK(context, TMtVmResource.class, resourceId));
                            }
                        }else{
                            mtVmShortcuts.setSku(null);
                            mtVmShortcuts.setBindType(null);
                            mtVmShortcuts.setResource(null);
                        }
                    }
                }

                List<TMtVmStrategy> stList = mtVm.getStrategy();
                boolean flag = false;
                for (TMtVmStrategy mtVmStrategy : stList) {
                    if (mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_SHORTCUTS)) {
                        mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TMtVmStrategy mtVmStrategy = context.newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_SHORTCUTS);
                    mtVmStrategy.setMtVm(mtVm);
                    mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
                }
            }

            context.commitChanges();
            String mcsMsg = mcsService.pushShortCuts(context, Arrays.asList(ids),null);
            if(StringUtil.isEmpty(mcsMsg)){
                sysLogService.saveSysLog("应用闪购键策略"+strategyId+"到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),context);
                return ReturnCode.SUCCESS.toMap();
            }else{
                return ReturnCode.MCS_FAIL.toMap();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
}
