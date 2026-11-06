package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmTextService;

import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by yangshuxi on 2016/12/9.
 */
@Service
public class MtVmTextServiceImpl implements IMtVmTextService{
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Resource
    private IMcsService mcsService;
    @Resource
    private ISysLogService sysLogService;
    @Override
    public Map<String, Object> saveText(TSysUser currentUser, ObjectContext context, String text, Integer[] ids, Date beginDate, Date endDate) {
        if(null==ids||null==beginDate||null==endDate)return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        for(Integer vmId:ids){
            TMtVm mtVm= Cayenne.objectForPK(context,TMtVm.class,vmId);
            TMtVmText mtVmText=mtVm.getMtText();
            mtVmText.setTextContents(text);
            mtVmText.setBeginDate(beginDate);
            mtVmText.setEndDate(endDate);
            mtVmText.setUtor(currentUser.getId());
            mtVmText.setUtime(new Date());

            List<TMtVmStrategy> stList=mtVm.getStrategy();
            boolean flag=false;
            for(TMtVmStrategy mtVmStrategy:stList){
                if(mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_TEXT)){
                    mtVmStrategy.clear(currentUser.getId());
                    flag=true;
                    break;
                }
            }
            if(!flag){
                TMtVmStrategy mtVmStrategy=context.newObject(TMtVmStrategy.class);
                mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_TEXT);
                mtVmStrategy.setMtVm(mtVm);
                mtVmStrategy.clear(currentUser.getId());
            }
        }
            context.commitChanges();
            String mcsMsg = mcsService.pushText(context, Arrays.asList(ids),null);
            if(StringUtil.isEmpty(mcsMsg)){
                sysLogService.saveSysLog("配置走马灯到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),context);
                return ReturnCode.SUCCESS.toMap();
            }else{
                return ReturnCode.MCS_FAIL.toMap();
            }
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveText(TSysUser currentUser, ObjectContext context, Integer strategyId, Integer[] ids, Date beginDate, Date endDate) {
        if(null==ids||null==beginDate||null==endDate||!IntegerUtils.isPositiveInteger(strategyId))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
        TStVmText stVmText=stVmStrategy.getStText();
        for(Integer vmId:ids){
            TMtVm mtVm= Cayenne.objectForPK(context,TMtVm.class,vmId);
            TMtVmText mtVmText=mtVm.getMtText();
            mtVmText.setTextContents(stVmText.getTextContents());
            mtVmText.setBeginDate(beginDate);
            mtVmText.setEndDate(endDate);
            mtVmText.setUtor(currentUser.getId());
            mtVmText.setUtime(new Date());

            List<TMtVmStrategy> stList=mtVm.getStrategy();
            boolean flag=false;
            for(TMtVmStrategy mtVmStrategy:stList){
                if(mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_TEXT)){
                    mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
                    flag=true;
                    break;
                }
            }
            if(!flag){
                TMtVmStrategy mtVmStrategy=context.newObject(TMtVmStrategy.class);
                mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_TEXT);
                mtVmStrategy.setMtVm(mtVm);
                mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
            }
        }
            context.commitChanges();
            String mcsMsg = mcsService.pushText(context, Arrays.asList(ids),null);
            if(StringUtil.isEmpty(mcsMsg)){
                sysLogService.saveSysLog("应用走马灯策略"+strategyId+"到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),context);
                return ReturnCode.SUCCESS.toMap();
            }else{
                return ReturnCode.MCS_FAIL.toMap();
            }
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
}
