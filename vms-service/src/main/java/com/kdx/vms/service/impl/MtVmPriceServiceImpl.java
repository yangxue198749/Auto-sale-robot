package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmPriceService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by yangshuxi on 2016/12/13.
 */
@Service
public class MtVmPriceServiceImpl implements IMtVmPriceService{
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Resource
    private IMcsService mcsService;
    @Resource
    private ISysLogService sysLogService;
    @Override
    public Map<String, Object> savePrice(TSysUser currentUser, ObjectContext context, Integer strategyId, Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        if(null==ids||!IntegerUtils.isPositiveInteger(strategyId))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            TStVmStrategy stVmStrategy= Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
            List<TStVmPrice> list=stVmStrategy.getStPrice();
            List<TMtVm> mtVmList = new ArrayList<TMtVm>();
            //List<TMtVm> mtVmLists = new ArrayList<TMtVm>();
            for(Integer vmId:ids){

                TMtVm mtVm=Cayenne.objectForPK(context,TMtVm.class,vmId);
                mtVmList.add(mtVm);

                List<TMtVmPrice> mtVmPrices=mtVm.getPrice();
                context.deleteObjects(mtVmPrices);

                //Restructure：不存副机
                for(TStVmPrice stVmPrice:list){
                    TMtVmPrice mtVmPrice=context.newObject(TMtVmPrice.class);
                    mtVmPrice.setCtor(currentUser.getId());
                    mtVmPrice.setMtVm(mtVm);
                    mtVmPrice.setCtime(new Date());
                    mtVmPrice.setRealPrice(stVmPrice.getRealPrice());
                    mtVmPrice.setSkuId(stVmPrice.getSku().getId());
                }

                List<TMtVmStrategy> stList=mtVm.getStrategy();
                boolean flag=false;
                for(TMtVmStrategy mtVmStrategy:stList){
                    if(mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_PRICE)){
                        mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    TMtVmStrategy mtVmStrategy=context.newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_PRICE);
                    mtVmStrategy.setMtVm(mtVm);
                    mtVmStrategy.update(currentUser.getId(),stVmStrategy.getStrategyName());
                }

            }
            context.commitChanges();
            for (TMtVm tMtVm : mtVmList) {
            	String mcsMsg = mcsService.sendSkuToMcs(context, tMtVm, false, true, null);
            	String newMcsMsg = mcsService.sendSkuInfoToMcs(context, tMtVm, false, true, null);
            	if(StringUtil.isEmpty(mcsMsg)){
                    mcsService.sendSkuPriceToMcs(context, tMtVm,true);
            	}
                if(StringUtil.isEmpty(newMcsMsg)){
                    mcsService.sendSkuPriceToMcs(context, tMtVm,true);
                }
			}
            sysLogService.saveSysLog("应用价格策略"+strategyId+"到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> originalPrice(TSysUser currentUser, ObjectContext context, Integer[] ids) {
        if(null==ids)return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            for(Integer vmId : ids){
                TMtVm mtVm = Cayenne.objectForPK(context,TMtVm.class,vmId);
                List<TMtVmPrice> mtVmPrices = mtVm.getPrice();
                context.deleteObjects(mtVmPrices);
                List<TMtVmStrategy> stList = mtVm.getStrategy();
                for(TMtVmStrategy mtVmStrategy:stList){
                    if(mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_PRICE)){
                        mtVmStrategy.clear(currentUser.getId());
                        break;
                    }
                }
            }
            context.commitChanges();
            //恢复原价后发送消息给mcs
            for(Integer vmId : ids){
                TMtVm mtVm = Cayenne.objectForPK(context,TMtVm.class,vmId);
                String mcsMsg = mcsService.sendSkuToMcs(context, mtVm, false, true, null);
                String newMcsMsg = mcsService.sendSkuInfoToMcs(context, mtVm, false, true, null);
                if(StringUtil.isEmpty(mcsMsg)){
                    mcsService.sendSkuPriceToMcs(context, mtVm,true);
                }
                if(StringUtil.isEmpty(newMcsMsg)){
                    mcsService.sendSkuPriceToMcs(context, mtVm,true);
                }
            }
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
}
