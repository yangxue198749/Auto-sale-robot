package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.SysElementDao;
import com.kdx.vms.orm.dao.SysRoleDao;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ISysRoleService;

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

import java.util.*;

/**
 *
 * Created by liyang on 16-12-1.
 */
@Service("sysRoleService")
public class SysRoleServiceImpl implements ISysRoleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource  private SysRoleDao     sysRoleDao;
    @Resource  private SysElementDao  sysElementDao;
    @Autowired private ISysLogService sysLogService;
    @Override
    public List getSysRoleCompany(TMtCompany company, String roleName, Integer pageSize) {
        logger.info("company:" + company);
        return sysRoleDao.findSysRoleCompany(company, roleName, pageSize);
    }

    public SystemCode addRole(String roleName, String roleDesc, TSysUser currUser) {
        logger.info("roleName:" + roleName + ", roleDesc:" + roleDesc + ", currUser:" + currUser);
        if (StringUtil.isEmpty(roleName) || null == currUser) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            TSysRole sysRole = context.newObject(TSysRole.class);
            sysRole.setCompany(currUser.getCompany());
            sysRole.setCtime(new Date());
            sysRole.setCtor(currUser.getId());
            sysRole.setRoleName(roleName);
            sysRole.setRoleDesc(roleDesc);
            sysRole.setRoleType(TSysRole.ROLE_TYPE_COMPANY);
            sysRole.setRoleLevel(TSysRole.ROLE_LEVEL_OTHER);
            context.commitChanges();
            sysLogService.saveSysLog("新建角色" +sysRole.getId(), TSysLog.INSERT, SyslogEnum.ROLE_CODE, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            if (null != context) context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public SystemCode editRole(Integer id, String roleName, String roleDesc, TSysUser currUser) {
        logger.info("id:" + id + ", roleName:" + roleName + ", roleDesc:" + roleDesc + ", currUser:" + currUser);
        if (StringUtil.isEmpty(roleName) || null == currUser) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            TSysRole sysRole = Cayenne.objectForPK(context, TSysRole.class, id);
            if (null == sysRole) return SystemCode.OBJECT_IS_NULL;
            sysRole.setUtime(new Date());
            sysRole.setUtor(currUser.getId());
            sysRole.setRoleName(roleName);
            sysRole.setRoleDesc(roleDesc);
            context.commitChanges();
            sysLogService.saveSysLog("编辑角色" + sysRole.getId(), TSysLog.UPDATE, SyslogEnum.ROLE_CODE, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            if (null != context) context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public SystemCode deleteRole(ObjectContext context, Integer userId) {
        logger.info("context:" + context + ", userId:" + userId);
        if (null == context || null == userId || userId <= 0) return SystemCode.PARAMETER_ERROR;
        try {
            // 新建一个子类content，这一步不是必须的，直接使用父累context提交出现异常时要进行本地回滚
            ObjectContext childContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
            // 缓存中查出对象
            TSysRole sysRole = Cayenne.objectForPK(childContext, TSysRole.class, userId);
            if (null == sysRole) return SystemCode.OBJECT_IS_NULL;
            childContext.deleteObject(sysRole);
            // 整体提交
            childContext.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    public SystemCode deleteRoles(ObjectContext context, List<Integer> userIds) {
        logger.info("context:" + context + ", userIds:" + userIds);
        if (null == context || null == userIds) return SystemCode.PARAMETER_ERROR;
        try {
            // 新建一个子类content，这一步不是必须的，直接使用父累context提交出现异常时要进行本地回滚
            ObjectContext childContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
            // 缓存中查出对象
            for (Integer userId : userIds) {
                TSysRole sysRole = Cayenne.objectForPK(childContext, TSysRole.class, userId);
                if (null != sysRole) {
                    if (sysRole.getRoleType() == TSysRole.ROLE_TYPE_PUBLIC || sysRole.getRoleType() == TSysRole.ROLE_TYPE_SYSTEM) return SystemCode.ROLE_CANNOT_DELETE;
                    childContext.deleteObject(sysRole);
                }
            }
            // 整体提交
            childContext.commitChanges();
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    public Boolean bindElement(TSysRole sysRole, List<Integer> ids, TSysUser currUser) {
        if (null == sysRole || null == ids || null == currUser) return false;
        ObjectContext context = currUser.getObjectContext();
        try {
            Date date = new Date();
            Integer currId = currUser.getId();
            context.deleteObjects(sysRole.getRoleElement());
            for (Integer id : ids) {
                TSysRoleElement sysRoleElement = context.newObject(TSysRoleElement.class);
                sysRoleElement.setElement(Cayenne.objectForPK(context, TSysElement.class, id));
                sysRoleElement.setCtor(currId);
                sysRoleElement.setCtime(date);
                sysRole.addToRoleElement(sysRoleElement);
            }
            context.commitChanges();
            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        return false;
    }

    public List<Map<String, Object>> getElementAll(TSysRole role) {
        List<Map<String, Object>> list = new ArrayList<>();
        List allList  = sysElementDao.getSysElementAll(role.getObjectContext());
        for (Object obj : allList) {
            Map<String, Object> map = new HashMap<>();
            TSysElement sysElement = (TSysElement) obj;
            map.put("id", sysElement.getId());
            map.put("pid", sysElement.getPid());
            map.put("name", sysElement.getElementName());
            list.add(map);
        }
        return list;
    }

	@Override
	public TSysRole getAdminRole(ObjectContext context, Integer level) {
		try {
			switch (level) {
			case TSysRole.ROLE_LEVEL_ADMIN:
				return sysRoleDao.findAdminRole(context, level, TSysRole.ROLE_TYPE_SYSTEM);
			case TSysRole.ROLE_LEVEL_ADMIN_2:
				return sysRoleDao.findAdminRole(context, level, TSysRole.ROLE_TYPE_PUBLIC);
			case TSysRole.ROLE_LEVEL_ADMIN_3:
				return sysRoleDao.findAdminRole(context, level, TSysRole.ROLE_TYPE_PUBLIC);
			default:
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
    public String getElementIdsForRole(TSysRole role){
        StringBuilder builder=new StringBuilder();
        List roleList = sysElementDao.getSysElementByRole(role);
        for (Object obj : roleList) {
            TSysElement sysElement = (TSysElement) obj;
            if(builder.length()>0)builder.append(",");
            builder.append(sysElement.getId());
        }
        return builder.toString();
    }

	@Override
	public List getElementAllBySystemAdmin(TSysRole sysRole) {
		List<Map<String, Object>> list = new ArrayList<>();
        List allList  = sysElementDao.getElementAllBySystemAdmin(sysRole);
        for (Object obj : allList) {
            Map<String, Object> map = new HashMap<>();
            TSysElement sysElement = ((TSysRoleElement) obj).getElement();
            map.put("id", sysElement.getId());
            map.put("pid", sysElement.getPid());
            map.put("name", sysElement.getElementName());
            list.add(map);
        }
        return list;
	}

	@Override
	public List getSysRolByCompany(TMtCompany company) {
		return sysRoleDao.findSysRoleByCompany(company);
	}
}
