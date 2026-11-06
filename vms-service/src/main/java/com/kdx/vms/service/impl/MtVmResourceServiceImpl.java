package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtVmResourceDao;
import com.kdx.vms.service.IMtVmResourceService;
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
 * Created by zhangguodong on 2017/5/23.
 */
@Service("mtVmResourceService")
public class MtVmResourceServiceImpl implements IMtVmResourceService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MtVmResourceDao mtVmResourceDao;
    @Autowired
    private ISysLogService sysLogService;
    @Override
    public List<TMtVmResource> findPage(TSysUser user, String name, Integer pageSize,Integer type) {
        try {
            return mtVmResourceDao.findPage(user,name,pageSize,type);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public SystemCode createMtVmResource(TSysUser user, String name, String resourceDesc, String resourceMd5, Long resourceSize, String resourceUrl) {
        try {
            TMtVmResource mtVmResource = user.getObjectContext().newObject(TMtVmResource.class);
            mtVmResource.setCtor(user.getId());
            mtVmResource.setCtime(new Date());
            mtVmResource.setDeleteMark(0);
            mtVmResource.setResourceType(TMtVmResource.MT_VM_SHORT_CURTS_RESOURCE_TYPE);
            mtVmResource.setResourceDesc(resourceDesc);
            mtVmResource.setResourceMd5(resourceMd5);
            mtVmResource.setResourceName(name);
            mtVmResource.setResourceSize(resourceSize.intValue());
            mtVmResource.setResourceUrl(resourceUrl);
            user.getObjectContext().commitChanges();
            sysLogService.saveSysLog("新增闪购键跳转页策略信息" + StringUtil.join(",", mtVmResource.getId()), TSysLog.INSERT, SyslogEnum.MT_THEME, user.getId(), user.getObjectContext());
            return SystemCode.SUCCESS;
        }catch (Exception e){
            user.getObjectContext().rollbackChanges();
            logger.error(e.getLocalizedMessage(),e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode delMtVmResources(TSysUser user, Integer[] ids) {
        logger.info("currUser:" + user + ", ids:" + StringUtil.join(",", ids));
        ObjectContext context = null;
        if (null == ids || null == user) {
            return SystemCode.PARAMETER_ERROR;
        }
        try {
            context = user.getObjectContext();
            for (Integer id : ids) {
                TMtVmResource mtVmResource = Cayenne.objectForPK(context, TMtVmResource.class, id);
                if (null != mtVmResource) {
                    mtVmResource.setDeleteMark(DeleteMark.DELETE.getCode());
                }
            }
            context.commitChanges();
            sysLogService.saveSysLog("删除(软删除)闪购键跳转页策略信息" + StringUtil.join(",", ids), TSysLog.DELETE, SyslogEnum.MT_THEME, user.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
        }
        return SystemCode.FAIL;
    }

}
