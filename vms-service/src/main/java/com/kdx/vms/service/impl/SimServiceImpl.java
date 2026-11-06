package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtSimDao;
import com.kdx.vms.service.ISimService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by liyang on 16-11-30.
 */
@Service("simService")
public class SimServiceImpl implements ISimService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource  private MtSimDao       mtSimDao;
    @Autowired private ISysLogService sysLogService;

    @Override
    public List getSimList(ObjectContext context ,TSysUser sysUser ,String simPhone, String simNumber, Integer serviceProvider, Boolean use, Integer pageSize) {
        logger.info("context:" + context + ", simPhone:" + simPhone + ", simNumber:" + simNumber + ", serviceProvider:" + serviceProvider + ", pageSize:" + pageSize);
        return mtSimDao.findMtSim(context, sysUser, simPhone, simNumber, serviceProvider, use, pageSize);
    }

    @Override
    public List getSimList(ObjectContext context, Integer pageSize) {
        logger.info("context:" + context + ", pageSize:" + pageSize);
        return mtSimDao.findMtSimPage(context, pageSize);
    }

    public SystemCode createSim(String simPhone, String simNumber, Integer serviceProvider, TSysUser currUser) {
        logger.info("simPhone:" + simPhone + ", simNumber:" + simNumber + ", serviceProvider:" + serviceProvider + ", currUser" + currUser);
        if (null == simPhone || null == simNumber || null == serviceProvider || null == currUser) {
            return SystemCode.PARAMETER_ERROR;
        }
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            List old = mtSimDao.findMtSim(context, simPhone, simNumber);
            if (null != old && old.size() == 0) {
                TMtSim mtSim = context.newObject(TMtSim.class);
                mtSim.setCtor(currUser.getId());
                mtSim.setCtime(new Date());
                mtSim.setDeleteMark(0);
                mtSim.setSimPhone(simPhone);
                mtSim.setSimNumber(simNumber);
                mtSim.setServiceProvider(serviceProvider);
                context.commitChanges();
                sysLogService.saveSysLog("创建sim卡信息" + mtSim.getId(), TSysLog.INSERT, SyslogEnum.SIM_CODE, currUser.getId(), context);
                return SystemCode.SUCCESS;
            } else {
                return SystemCode.SIM_PHONE_EXITS;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
        }
        return SystemCode.CONTEXT_COMMIT_FAIL;
    }

    @Override
    public SystemCode editSim(Integer simId, String simPhone, String simNumber, Integer serviceProvider, TSysUser currUser) {
        logger.info("simId:" + simId + ", simPhone:" + simPhone + ", simNumber:" + simNumber + ", serviceProvider:" + serviceProvider + ", currUser" + currUser);
        if (null == simId || null == simPhone || null == simNumber || null == serviceProvider || null == currUser) {
            return SystemCode.PARAMETER_ERROR;
        }
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            TMtSim mtSim = mtSimDao.findMtSimPk(context, simId);
            if (null == mtSim) return SystemCode.OBJECT_IS_NULL;
            List old = mtSimDao.findMtSim(context, simPhone, simNumber);
            // 这里要检查 想要修改的值 是否已经存在：用修改值查出结果集，用当前对象在结果集中筛选，集合中存在其他对象说明有重复的
            if (!hasOther(mtSim, old)) {
                mtSim.setUtor(currUser.getId());
                mtSim.setUtime(new Date());
                mtSim.setSimPhone(simPhone);
                mtSim.setSimNumber(simNumber);
                mtSim.setServiceProvider(serviceProvider);
                context.commitChanges();
                sysLogService.saveSysLog("修改sim卡信息" + mtSim.getId(), TSysLog.UPDATE, SyslogEnum.SIM_CODE, currUser.getId(), context);
                return SystemCode.SUCCESS;
            } else {
                return SystemCode.SIM_PHONE_EXITS;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
        }
        return SystemCode.FAIL;
    }

    /**
     * 检查结果中
     * @param mtSim
     * @param list
     * @return
     */
    private boolean hasOther(TMtSim mtSim, List list) {
        if (null == mtSim || null == list || list.size() == 0) return false;
        for (Object obj : list) {
            TMtSim mtSimTemp = (TMtSim) obj;
            if (!mtSim.getId().equals(mtSimTemp.getId())) return true;
        }
        return false;
    }

    public SystemCode deleteSim(List<Integer> simIds, TSysUser currUser) {
        logger.info("simId:" + (null == simIds ? null : simIds.size()) + ", currUser" + currUser);
        ObjectContext context = null;
        if (null == simIds || null == currUser) {
            return SystemCode.PARAMETER_ERROR;
        }
        try {
            context = currUser.getObjectContext();
            for (Integer simId : simIds) {
                TMtSim mtSim = Cayenne.objectForPK(context, TMtSim.class, simId);
                context.deleteObject(mtSim);
            }
            context.commitChanges();
            sysLogService.saveSysLog("删除sim卡信息" + StringUtil.join(",", simIds), TSysLog.DELETE, SyslogEnum.SIM_CODE, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
        }
        return SystemCode.FAIL;
    }

}