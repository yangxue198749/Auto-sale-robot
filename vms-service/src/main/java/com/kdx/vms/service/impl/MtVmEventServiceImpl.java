package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtVmEventMasterDao;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmEventService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by yangshuxi on 2016/12/6.
 */
@Service("mtVmEventService")
public class MtVmEventServiceImpl implements IMtVmEventService{
    @Autowired
    MtVmEventMasterDao mtVmEventMasterDao;
    @Autowired
    IMcsService mcsService;
    @Resource
    private MyJedisCluster myJedisCluster;
    @Resource
    private ISysLogService sysLogService;
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Override
    public List<TMtVmEventMaster> findMtEventMaster(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer pageSize) {
        return mtVmEventMasterDao.findMtEventMaster(context,currentUser.getCompany(),nodeName,innerCode,province,city,area,businessDistrictId,pageSize);
    }

    @Override
    public List<TMtVm> findSkuEventVmList(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId ,Integer[] removeIds) {
        return mtVmEventMasterDao.findSkuEventVmList(context,currentUser.getCompany(),nodeName,innerCode,province,city,area,businessDistrictId,removeIds);
    }

    @Override
    public List<TMtVm> findVmEventVmList(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId ,Integer[] removeIds) {
        return mtVmEventMasterDao.findVmEventVmList(context,currentUser.getCompany(),nodeName,innerCode,province,city,area,businessDistrictId,removeIds);
    }

    @Override
    public Map<String, Object> saveMtVmEvent(TSysUser currentUser, ObjectContext context, Integer[] vmIds, Integer strategyId, Date beginDate, Date endDate, String startTime, String endTime,Integer eventType) {
        if(null==vmIds||!IntegerUtils.isPositiveInteger(strategyId)||null==beginDate||null==endDate||StringUtil.isEmpty(startTime)||StringUtil.isEmpty(endTime)||!IntegerUtils.isPositiveInteger(eventType))return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            List<TMtVm> vmList=new ArrayList<>();
            List<TMtVm> vmLists=new ArrayList<>();
            Date start = DateUtils.getDate2(DateUtils.getTime1()+ " "+ startTime+":00");
            Date end = DateUtils.getDate2(DateUtils.getTime1()+ " " + endTime+":00");
            if(start.after(end)){
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("code",-100);
                resultMap.put("type","error");
                resultMap.put("message","结束时间应该大于开始时间");
                return resultMap;
            }
        for(Integer vmId:vmIds){
            if(!IntegerUtils.isPositiveInteger(vmId))continue;
            TMtVm mtVm= Cayenne.objectForPK(context,TMtVm.class,vmId);
            vmList.add(mtVm);
            List<TMtVmEventMaster> list=mtVm.getEvent();
            if(null!=list&&!list.isEmpty()) {
                for (TMtVmEventMaster event : list) {
                    if (event.getEventType().equals(eventType)) {
                        context.deleteObject(event);
                        context.deleteObjects(event.getEvents());
                        break;
                    }
                }
            }
            TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
            TMtVmEventMaster mtVmEventMaster=context.newObject(TMtVmEventMaster.class);
            mtVmEventMaster.setEventName(stVmStrategy.getStrategyName());
            mtVmEventMaster.setEventType(eventType);
            mtVmEventMaster.setBeginDate(beginDate);
            mtVmEventMaster.setEndDate(endDate);
            mtVmEventMaster.setStartTime(startTime);
            mtVmEventMaster.setEndTime(endTime);
            mtVmEventMaster.setCtime(new Date());
            mtVmEventMaster.setCtor(currentUser.getId());
            mtVmEventMaster.setEventDesc(stVmStrategy.getStrategyDesc());
            mtVmEventMaster.setEventId(stVmStrategy.getStrategyId());
            mtVmEventMaster.setMtVm(mtVm);
            List<TStVmEvent> eventList=stVmStrategy.getStEvent();
            for(TStVmEvent stVmEvent:eventList){
                TMtVmEvent mtVmEvent=context.newObject(TMtVmEvent.class);
                mtVmEvent.setProperties(stVmEvent);
                mtVmEvent.setMaster(mtVmEventMaster);
            }
        }

            context.commitChanges();
            vmList.stream().filter(vm -> null != vm.getChildrenVm() && vm.getChildrenVm().size() > 0).forEach(vm -> {
                vmLists.addAll(vm.getChildrenVm());
            });
            vmLists.addAll(vmList);
            for(TMtVm vm:vmLists){
                mcsService.sendSkuPriceToMcs(context,vm,false);
            }
            sysLogService.saveSysLog("应用促销策略"+strategyId+"到售货机"+ StringUtil.join(",",vmIds),TSysLog.SAVE, SyslogEnum.MT_VM_EVENT_CODE,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> deleteEvent(TSysUser currentUser, ObjectContext context, Integer[] mtVmEventIds) {
        if(null==mtVmEventIds)return ReturnCode.PARAMETER_ERROR.toMap();
        try {
        List<Integer> vmIdList=new ArrayList<>();
        List<Integer> vmEventMasterList=Arrays.asList(mtVmEventIds);
        List<TMtVm> vmList= new ArrayList<>();
        logger.debug(currentUser.getUserAccount()+"执行删除应用的促销策略"+"Id为"+mtVmEventIds);
        for(Integer mtVmEventId:mtVmEventIds){
            TMtVmEventMaster  mtVmEventMaster= Cayenne.objectForPK(context, TMtVmEventMaster.class, mtVmEventId);
            vmIdList.add(mtVmEventMaster.getMtVm().getId());
            vmList.add(mtVmEventMaster.getMtVm());
            List list=mtVmEventMaster.getEvents();

            if(null != list){
                context.deleteObjects(list);
            }
            //删除redis中的策略
            context.deleteObject(mtVmEventMaster);
        }
            for (int i = 0; i <mtVmEventIds.length ; i++) {
                myJedisCluster.delKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK+":"+vmIdList.get(i)+":"+vmEventMasterList.get(i));
            }
            context.commitChanges();
            for(TMtVm vm:vmList){
                mcsService.sendSkuPriceToMcs(context,vm,true);
            }
            sysLogService.saveSysLog("取消应用促销策略"+ StringUtil.join(",",mtVmEventIds),TSysLog.SAVE, SyslogEnum.MT_VM_EVENT_CODE,currentUser.getId(),context);
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }
}
