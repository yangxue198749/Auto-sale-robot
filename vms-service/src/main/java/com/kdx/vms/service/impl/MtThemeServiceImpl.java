package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtThemeDao;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtThemeService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by liyang on 2017/5/3.
 */
@Service("mtThemeService")
public class MtThemeServiceImpl implements IMtThemeService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource  private MtThemeDao     mtThemeDao;
    @Autowired private ISysLogService sysLogService;
    @Autowired private IMcsService    mcsService;

    @Override
    public List getThemesByName(TSysUser currUser, String themeName, Integer pageSize) {
        LOGGER.info("currUser:" + currUser + ", themeName:" + themeName);
        return mtThemeDao.findThemesByName(currUser.getObjectContext(), themeName, pageSize);
    }

    @Override
    public SystemCode deleteThemes(TSysUser currUser, List<Integer> themeIds) {
        LOGGER.info("currUser:" + currUser + ", themeIds:" + StringUtil.join(",", themeIds));
        ObjectContext context = null;
        if (null == themeIds || null == currUser) {
            return SystemCode.PARAMETER_ERROR;
        }
        try {
            context = currUser.getObjectContext();
            for (Integer themeId : themeIds) {
                TMtTheme theme = Cayenne.objectForPK(context, TMtTheme.class, themeId);
                if (null != theme) {
                    theme.setDeleteMark(DeleteMark.DELETE);
                }
            }
            context.commitChanges();
            sysLogService.saveSysLog("删除(软删除)皮肤信息" + StringUtil.join(",", themeIds), TSysLog.DELETE, SyslogEnum.MT_THEME, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode createTheme(TSysUser currUser, String themeName, String themeDesc, String themeUrl, String themePreview, Long size, String md5) {
        LOGGER.info("currUser:" + currUser + ", themeName:" + themeName + ", themeDesc" + themeDesc + ", themeUrl:" + themeUrl + ", themePreview:" + themePreview + ", size:" + size + ", md5:" + md5);
        try {
            ObjectContext context = ObjectContextFactory.getInstance().getChildObjectContext(currUser.getObjectContext());
            TMtTheme theme = context.newObject(TMtTheme.class);
            theme.setCtime(new Date());
            theme.setCtor(currUser.getId());
            theme.setUtime(theme.getCtime());
            theme.setUtor(theme.getCtor());
            theme.setDeleteMark(DeleteMark.EXIST);
            theme.setThemeName(themeName);
            theme.setThemeDesc(themeDesc);
            theme.setThemeUrl(themeUrl);
            theme.setPreviewUrls(themePreview);
            theme.setThemeMd5(md5);
            theme.setThemeSize(size);
            context.commitChanges();
            sysLogService.saveSysLog("新增皮肤信息" + StringUtil.join(",", theme.getId()), TSysLog.INSERT, SyslogEnum.MT_THEME, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode bindTheme(TSysUser currUser, Integer[] vmIds, Integer themeId) {
        LOGGER.info("currUser:" + currUser + ", vmId:" + Arrays.toString(vmIds) + ", themeId" + themeId);
        try {
            ObjectContext context = currUser.getObjectContext();
            if (null == context) {
                throw new NullPointerException("context is null.");
            }
            if (null == vmIds) {
                throw new  NullPointerException("vmIds is null.");
            }
            if (!IntegerUtils.isPositiveInteger(themeId)) {
                throw new IllegalArgumentException("themeId illegal.");
            }
            ObjectContext childContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
            TMtTheme theme = Cayenne.objectForPK(childContext, TMtTheme.class, themeId);
            if (null != theme) {
                for (Integer vmId : vmIds) {
                    TMtVm mtVm = Cayenne.objectForPK(childContext, TMtVm.class, vmId);
                    if (null != mtVm) {
                        childContext.deleteObjects(mtVm.getVmTheme());
                        TMtVmTheme vmTheme = childContext.newObject(TMtVmTheme.class);
                        vmTheme.setMtVm(mtVm);
                        vmTheme.setTheme(theme);
                        vmTheme.setCtime(new Date());
                        vmTheme.setCtor(currUser.getId());
                        vmTheme.setUtime(vmTheme.getCtime());
                        vmTheme.setUtor(vmTheme.getCtor());
                    }
                }
            }
            childContext.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode setFormat(TSysUser currUser, Integer[] vmIds, Integer formatCode) {
        LOGGER.info("currUser:" + currUser + ", vmId:" + Arrays.toString(vmIds) + ", formatCode" + formatCode);
        try {
            ObjectContext context = currUser.getObjectContext();
            if (null == context) {
                throw new NullPointerException("context is null.");
            }
            if (null == vmIds) {
                throw new  NullPointerException("vmIds is null.");
            }
            if (!IntegerUtils.isPositiveInteger(formatCode)) {
                throw new IllegalArgumentException("formatCode illegal.");
            }
            ObjectContext childContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
            for (Integer vmId : vmIds) {
                TMtVm mtVm = Cayenne.objectForPK(childContext, TMtVm.class, vmId);
                if (null != mtVm) {
                    TMtVmThemeFormat tMtVmThemeFormat = mtVm.getThemeFormat();
                    if (null == tMtVmThemeFormat) tMtVmThemeFormat = childContext.newObject(TMtVmThemeFormat.class);
                    tMtVmThemeFormat.setThemeFormat(formatCode);
                    mtVm.setThemeFormat(tMtVmThemeFormat);
                }
            }
            childContext.commitChanges();
            for (Integer vmId : vmIds) {
                TMtVm mtVm = Cayenne.objectForPK(childContext, TMtVm.class, vmId);
                mcsService.sendVmBaseInfo(childContext, mtVm.getInnerCode(), "vms", System.currentTimeMillis());
            }
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode setConfiguration(ObjectContext context, Integer resourceId, Integer vmId, String fileName, Integer index) {
        try {
            TMtVmThemeEntrance themeEntrance = findByVmId(context,index,vmId);
            if(null != themeEntrance){
                if(null != fileName){
                    themeEntrance.setImgUrl(fileName);
                }
                if(null != resourceId){
                    themeEntrance.setResource(Cayenne.objectForPK(context,TMtVmResource.class,resourceId));
                }
            }else {
                TMtVmThemeEntrance tMtVmThemeEntrance = context.newObject(TMtVmThemeEntrance.class);
                tMtVmThemeEntrance.setMtvm(Cayenne.objectForPK(context,TMtVm.class,vmId));
                tMtVmThemeEntrance.setImgUrl(fileName);
                tMtVmThemeEntrance.setEntranceIndex(index);
                TMtVmResource tMtVmResource = Cayenne.objectForPK(context,TMtVmResource.class,resourceId);
                tMtVmThemeEntrance.setResource(tMtVmResource);
            }
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            context.rollbackChanges();
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode delectConfiguration(ObjectContext context, Integer vmId, Integer index) {
        try {

            Expression expression = ExpressionFactory.matchExp(TMtVmThemeEntrance.MTVM_PROPERTY,vmId);
            expression = expression.andExp(ExpressionFactory.matchExp(TMtVmThemeEntrance.ENTRANCE_INDEX_PROPERTY,index));
            SelectQuery selectQuery = new SelectQuery(TMtVmThemeEntrance.class, expression);
            List<TMtVmThemeEntrance> list = context.performQuery(selectQuery);
            if(null != list && list.size() >0){
                context.deleteObject(list.get(0));
            }
            context.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            context.rollbackChanges();
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public TMtVmThemeEntrance findByVmId(ObjectContext context, Integer index, Integer vmId) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVmThemeEntrance.MTVM_PROPERTY, vmId);
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmThemeEntrance.ENTRANCE_INDEX_PROPERTY, index));
            SelectQuery select = new SelectQuery(TMtVmThemeEntrance.class, qualifier);
            List<TMtVmThemeEntrance> themeEntrances = context.performQuery(select);
            if(null != themeEntrances && themeEntrances.size()>0){
                return themeEntrances.get(0);
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
