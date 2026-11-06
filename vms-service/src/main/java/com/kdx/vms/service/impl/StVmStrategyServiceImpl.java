package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IStVmStrategyService;

import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by yangshuxi on 2016/12/1.
 */
@Service("stVmStrategyService")
public class StVmStrategyServiceImpl implements IStVmStrategyService {

	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    
	@Resource
	private StVmStrategyDao stVmStrategyDao;
    @Autowired
    private ISysLogService sysLogService;
    @Override
    public Map<String, Object> deleteStrategy(TSysUser currentUser, ObjectContext context, Integer[] strategyIds, Integer strategyType) {
        if(null==strategyIds)return ReturnCode.PARAMETER_ERROR.toMap();
        for(Integer strategyId:strategyIds){
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
//            List list=null;
//            switch (strategyType) {
//                case TStVmStrategy.TYPE_ST_VM_CHANNEL:
//                    list=stVmStrategy.getStChannel();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE:
//                    list=stVmStrategy.getStEnergySave();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_EVENT:
//                    list=stVmStrategy.getStEvent();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_PRICE:
//                    list=stVmStrategy.getStPrice();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_SHORTCUTS:
//                    list=stVmStrategy.getStShortcuts();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_SKU_SORT:
//                    list=stVmStrategy.getStSkuSort();
//                    break;
//                case TStVmStrategy.TYPE_ST_VM_TEXT:
//                    TStVmText text = stVmStrategy.getStText();
//                    context.deleteObject(text);
//                    break;
//            }
//            if(null != list){
//            	context.deleteObjects(list);
//            }
            context.deleteObject(stVmStrategy);
        }
        try {
            context.commitChanges();
            logger.debug(currentUser.getUserAccount()+"执行删除策略"+"策略Id为"+StringUtil.join(",",strategyIds));
            sysLogService.saveSysLog("删除策略"+ StringUtil.join(",",strategyIds), TSysLog.DELETE, SyslogEnum.ST_VM_STRATEGY,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
	@Override
	public TStVmStrategy findByPk(ObjectContext context, Integer strategyId) {
		try {
			return stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
