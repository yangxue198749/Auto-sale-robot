package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtNodeDao;
import com.kdx.vms.service.IMtNodeService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/28.
 */
@Service("mtNodeService")
public class MtNodeServiceImpl implements IMtNodeService {
    @Autowired
    private MtNodeDao mtNodeDao;
    @Autowired
    private ISysLogService sysLogService;
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, Object> saveNode(TSysUser currentUser, ObjectContext context, TMtNode mtNode, Integer partnerId, Integer deliveryId, Integer repairId) {
        if (null == mtNode || !IntegerUtils.isPositiveInteger(deliveryId) || !IntegerUtils.isPositiveInteger(repairId))
            return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            mtNode.setCtime(new Date());
            mtNode.setCtor(currentUser.getId());
            mtNode.setCompany(currentUser.getCompany());
            if (IntegerUtils.isPositiveInteger(partnerId)) {
                mtNode.setPartner(Cayenne.objectForPK(context, TMtPartner.class, partnerId));
            }
            mtNode.setDelivery(Cayenne.objectForPK(context, TSysUser.class, deliveryId));
            mtNode.setRepair(Cayenne.objectForPK(context, TSysUser.class, repairId));

            context.registerNewObject(mtNode);
            context.commitChanges();

//            TErpDataSync erpDataSync = context.newObject(TErpDataSync.class);
//            erpDataSync.setCtime(new Date());
//            erpDataSync.setCtor(currentUser.getId());
//            erpDataSync.setDataContent(NodeObject.getInstance(mtNode, context, TErpDataSync.OPERATE_ADD).getJson());
//            erpDataSync.setSyncUrl(ErpConfig.URL_PUSH_NODE);
//            erpDataSync.setDataType("点位信息");
//            erpDataSync.setSyncMark(TErpDataSync.SYNC_FALSE);
            context.commitChanges();

            sysLogService.saveSysLog("新增点位" + mtNode.getId(), TSysLog.INSERT, SyslogEnum.NODE_CODE, currentUser.getId(), context);

//            PushSingleThread pushSingleThread = new PushSingleThread(context, erpDataSync);
//            Thread thread = new Thread(pushSingleThread);
//            thread.start();

            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> saveNode(TSysUser currentUser, ObjectContext context, TMtNode mtNode, Integer nodeId, Integer partnerId, Integer deliveryId, Integer repairId) {
        if (null == mtNode || !IntegerUtils.isPositiveInteger(nodeId) || !IntegerUtils.isPositiveInteger(deliveryId) || !IntegerUtils.isPositiveInteger(repairId))
            return ReturnCode.PARAMETER_ERROR.toMap();
        context.rollbackChanges();
        ObjectContext objectContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
        try {
            TMtNode tMtNode = Cayenne.objectForPK(objectContext, TMtNode.class, nodeId);
            tMtNode.setALLProperties(mtNode);
            tMtNode.setUtime(new Date());
            if (IntegerUtils.isPositiveInteger(partnerId)) {
                tMtNode.setPartner(Cayenne.objectForPK(objectContext, TMtPartner.class, partnerId));
            }
            tMtNode.setUtor(currentUser.getId());
            tMtNode.setCompany(Cayenne.objectForPK(objectContext, TMtCompany.class, currentUser.getCompany().getId()));
            tMtNode.setDelivery(Cayenne.objectForPK(objectContext, TSysUser.class, deliveryId));
            tMtNode.setRepair(Cayenne.objectForPK(objectContext, TSysUser.class, repairId));
//            TErpDataSync erpDataSync = context.newObject(TErpDataSync.class);
//            erpDataSync.setCtime(new Date());
//            erpDataSync.setCtor(currentUser.getId());
//            erpDataSync.setDataContent(NodeObject.getInstance(tMtNode, context, TErpDataSync.OPERATE_UPDATE).getJson());
//            erpDataSync.setSyncUrl(ErpConfig.URL_PUSH_NODE);
//            erpDataSync.setDataType("点位信息");
//            erpDataSync.setSyncMark(TErpDataSync.SYNC_FALSE);

            objectContext.commitChanges();
            sysLogService.saveSysLog("修改点位" + tMtNode.getId(), TSysLog.UPDATE, SyslogEnum.NODE_CODE, currentUser.getId(), context);

//            PushSingleThread pushSingleThread = new PushSingleThread(context, erpDataSync);
//            Thread thread = new Thread(pushSingleThread);
//            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            objectContext.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> deleteNode(TSysUser currentUser, ObjectContext context, Integer[] nodeIds) {
        if (null == nodeIds) return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            TErpDataSync[] erpDataSyncs = new TErpDataSync[nodeIds.length];
            int i = 0;
            for (Integer nodeId : nodeIds) {
                TMtNode tMtNode = Cayenne.objectForPK(context, TMtNode.class, nodeId);
//
//                erpDataSyncs[i] = context.newObject(TErpDataSync.class);
//                erpDataSyncs[i].setCtime(new Date());
//                erpDataSyncs[i].setCtor(currentUser.getId());
//                erpDataSyncs[i].setDataContent(NodeObject.getInstance(tMtNode, context, TErpDataSync.OPERATE_DELETE).getJson());
//                erpDataSyncs[i].setSyncUrl(ErpConfig.URL_PUSH_NODE);
//                erpDataSyncs[i].setDataType("点位信息");
//                erpDataSyncs[i].setSyncMark(TErpDataSync.SYNC_FALSE);
                i++;
                context.deleteObject(tMtNode);
            }
            context.commitChanges();
            sysLogService.saveSysLog("删除点位" + StringUtil.join(",", nodeIds), TSysLog.DELETE, SyslogEnum.NODE_CODE, currentUser.getId(), context);

//            PushSingleThread pushSingleThread = new PushSingleThread(context, erpDataSyncs);
//            Thread thread = new Thread(pushSingleThread);
//            thread.start();

            return ReturnCode.SUCCESS.toMap();
        } catch (DeleteDenyException e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.DELETE_DENY.toMap();
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> setDelivery(TSysUser currentUser, ObjectContext context, TSysUser delivery, Integer[] nodeIds) {
        if (null == nodeIds || null == delivery) return ReturnCode.PARAMETER_ERROR.toMap();
//        TErpDataSync[] erpDataSyncs = new TErpDataSync[nodeIds.length];
        int i = 0;
        for (Integer nodeId : nodeIds) {
            TMtNode tMtNode = Cayenne.objectForPK(context, TMtNode.class, nodeId);
            tMtNode.setDelivery(delivery);
//
//            erpDataSyncs[i] = context.newObject(TErpDataSync.class);
//            erpDataSyncs[i].setCtime(new Date());
//            erpDataSyncs[i].setCtor(currentUser.getId());
//            erpDataSyncs[i].setDataContent(NodeObject.getInstance(tMtNode, context, TErpDataSync.OPERATE_UPDATE).getJson());
//            erpDataSyncs[i].setSyncUrl(ErpConfig.URL_PUSH_NODE);
//            erpDataSyncs[i].setDataType("点位信息");
//            erpDataSyncs[i].setSyncMark(TErpDataSync.SYNC_FALSE);
            i++;
        }
        try {
            context.commitChanges();
            sysLogService.saveSysLog("给点位" + StringUtil.join(",", nodeIds) + "配置补货员" + delivery.getId(), TSysLog.SAVE, SyslogEnum.NODE_CODE, currentUser.getId(), context);
//            PushSingleThread pushSingleThread = new PushSingleThread(context, erpDataSyncs);
//            Thread thread = new Thread(pushSingleThread);
//            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> setRepair(TSysUser currentUser, ObjectContext context, TSysUser repair, Integer[] nodeIds) {
        if (null == nodeIds || null == repair) return ReturnCode.PARAMETER_ERROR.toMap();
//        TErpDataSync[] erpDataSyncs = new TErpDataSync[nodeIds.length];
        int i = 0;
        try {
            for (Integer nodeId : nodeIds) {
                TMtNode tMtNode = Cayenne.objectForPK(context, TMtNode.class, nodeId);
                tMtNode.setRepair(repair);

//                erpDataSyncs[i] = context.newObject(TErpDataSync.class);
//                erpDataSyncs[i].setCtime(new Date());
//                erpDataSyncs[i].setCtor(currentUser.getId());
//                erpDataSyncs[i].setDataContent(NodeObject.getInstance(tMtNode, context, TErpDataSync.OPERATE_UPDATE).getJson());
//                erpDataSyncs[i].setSyncUrl(ErpConfig.URL_PUSH_NODE);
//                erpDataSyncs[i].setDataType("点位信息");
//                erpDataSyncs[i].setSyncMark(TErpDataSync.SYNC_FALSE);
                i++;
            }
            context.commitChanges();
            sysLogService.saveSysLog("给点位" + StringUtil.join(",", nodeIds) + "配置维修员" + repair.getId(), TSysLog.SAVE, SyslogEnum.NODE_CODE, currentUser.getId(), context);

//            PushSingleThread pushSingleThread = new PushSingleThread(context, erpDataSyncs);
//            Thread thread = new Thread(pushSingleThread);
//            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public TMtNode findNode(ObjectContext context, Integer nodeId) {
        return Cayenne.objectForPK(context, TMtNode.class, nodeId);
    }

    @Override
    public List findNodeList(TSysUser currentUser, ObjectContext context, String searchType, String searchValue, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer pageSize) {
        String nodeName = null;
        String deliveryName = null;
        String repairName = null;
        String propertyName = null;
        String partnerName = null;
        if (null != searchType) {
            switch (searchType) {
                case "1":
                    nodeName = searchValue;
                    break;
                case "2":
                    deliveryName = searchValue;
                    break;
                case "3":
                    repairName = searchValue;
                    break;
                case "4":
                    propertyName = searchValue;
                    break;
                case "5":
                    partnerName = searchValue;
                    break;
            }
        }
        return mtNodeDao.getMtNodeList(context, deliveryName, repairName, partnerName, nodeName, propertyName, currentUser.getCompany(), province, city, area, businessDistrictId, pageSize);
    }

}