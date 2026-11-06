package com.kdx.vms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.*;

import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdx.vms.orm.dao.MtCompanyDao;
import com.kdx.vms.orm.dao.SysUserDao;
import com.kdx.vms.service.ISysUserService;

@Service("sysUserService")
public class SysUserServiceImpl implements ISysUserService {

	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Resource  private SysUserDao     sysUserDao;
	@Resource  private MtCompanyDao   mtCompanyDao;
    @Autowired private ISysLogService sysLogService;

	@Override
	public Boolean updateUserPwd(ObjectContext context, TSysUser tSysUser, String pwd) {
        try {
            pwd = Md5Util.md5(pwd);
            context.registerNewObject(tSysUser);
            tSysUser.setUserPassword(pwd);
            tSysUser.setFailCount(0);
            tSysUser.setLockMark(0);
            Date date = new Date();
            tSysUser.setLoginTime(date);
            tSysUser.setUtime(date);
            tSysUser.setUtor(tSysUser.getId());
            context.commitChanges();
            sysLogService.saveSysLog("更新用户密码" + tSysUser.getId(), TSysLog.UPDATE, SyslogEnum.USER_CODE, tSysUser.getId(), context);
            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
	}

	@Override
	public Boolean updateUserName(ObjectContext context, TSysUser tSysUser, String userName) {
		try {
			context.registerNewObject(tSysUser);
			tSysUser.setUserName(userName);
			tSysUser.setUtime(new Date());
            tSysUser.setUtor(tSysUser.getId());
            context.commitChanges();
            sysLogService.saveSysLog("更新用户名称" + tSysUser.getId(), TSysLog.UPDATE, SyslogEnum.USER_CODE, tSysUser.getId(), context);
            return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public Boolean updateUserPhone(ObjectContext context, TSysUser tSysUser, String userPhone) {
		try {
			context.registerNewObject(tSysUser);
			tSysUser.setUserPhone(userPhone);
			tSysUser.setUtime(new Date());
            tSysUser.setUtor(tSysUser.getId());
            context.commitChanges();
            sysLogService.saveSysLog("更新用户电话" + tSysUser.getId(), TSysLog.UPDATE, SyslogEnum.USER_CODE,  tSysUser.getId(), context);
            return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public TSysUser getUserByAccount(ObjectContext context, String account) {
        try {
            List list = sysUserDao.findSysUserByAccount(context, account,false);
            return list != null && list.size() > 0 ? (TSysUser) list.get(0) : null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
	}

    @Override
    public TSysUser getAliveUserByAccount(ObjectContext context, String account) {
        try {
            List list = sysUserDao.findSysUserByAccount(context, account,true);
            return list != null && list.size() > 0 ? (TSysUser) list.get(0) : null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

	@Override
	public List<TSysUser> getTSysUserByName(String userName, ObjectContext context) {
		return sysUserDao.findSysUserByName(context, userName);
	}

	@Override
	public Boolean isAdminUser(ObjectContext context, TSysUser tSysUser) {
		try {
			TSysUser adminUser = mtCompanyDao.findAdminSysUserByCompany(context, tSysUser.getCompany().getId(), tSysUser.getCompany().getCompanyLevel());
			if(adminUser != null && adminUser.getId() == tSysUser.getId()) return true;
			return false;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

    @Override
    public List getSysUserForCompany(ObjectContext context, String searchType, String searchValue, Integer mtCompanyId, Integer pageSize) {
        return sysUserDao.findSysUserForCompany(context, searchType, searchValue, mtCompanyId, pageSize);
    }

    @Override
    public List getSysUserForCompany(ObjectContext context, String searchType, String searchValue, TMtCompany mtCompanyId, Integer pageSize) {
        return sysUserDao.findSysUserForCompany(context, searchType, searchValue, mtCompanyId, pageSize);
    }



    public SystemCode deleteUser(List<Integer> userIdList, TSysUser currUser) {
        logger.info("userIdList:" + (null == userIdList ? null : userIdList.size()) + ", currUser:" + currUser);
        if (null == userIdList || null == currUser) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            for (Integer userId : userIdList) {
                if (IntegerUtils.isPositiveInteger(userId)) {
                    TSysUser sysUser = Cayenne.objectForPK(context, TSysUser.class, userId);
                    // 软删除
                    sysUser.setDeleteMark(TSysUser.DELETE_MARK_DELETE);
//                    context.deleteObject(sysUser);
                    context.deleteObjects(sysUser.getBind());
                }
            }
            context.commitChanges();
            sysLogService.saveSysLog("删除用户（软删除）" + StringUtil.join(",", userIdList), TSysLog.DELETE, SyslogEnum.USER_CODE, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    public SystemCode createUser(String userAccount, String userName, String userPhone, Integer companyId, List<Integer> roleIdList, TSysUser currUser) {
        logger.info("userAccount:" + userAccount + ", userName:" + userName + ", userPhone:" + userPhone + ", companyId:" + companyId + ", roleId:" + (null == roleIdList ? null : roleIdList.size()) + ", currUser:" + currUser);
        if (StringUtil.isEmpty(userAccount)  || StringUtil.isEmpty(userName) || StringUtil.isEmpty(userPhone) || null == companyId || companyId <= 0 || null == roleIdList || null == currUser) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            userAccount = userAccount.trim();
            context = currUser.getObjectContext();
            //账号排重
            TSysUser user = getUserByAccount(context,userAccount);
            if(null!=user){
                return SystemCode.USER_IS_EXIST;
            }
            // 新建一个用户对象
            TSysUser sysUser = context.newObject(TSysUser.class);
            sysUser.setUserAccount(userAccount);
            sysUser.setUserName(userName);
            sysUser.setUserPhone(userPhone);
            sysUser.setDeleteMark(0);
            sysUser.setFailCount(0);
            String password = RandomStringUtils.random(6, false, true);
            sysUser.setUserPassword(Md5Util.md5(password));

            // 查出(缓存)id对应的公司对象
            TMtCompany mtCompany = Cayenne.objectForPK(context, TMtCompany.class, companyId);
            if (null == mtCompany) return SystemCode.OBJECT_IS_NULL;
            // 设置用户所属公司
            sysUser.setCompany(mtCompany);
            // 查出(缓存)id对应的角色名称
            for (Integer roleId : roleIdList) {
                TSysRole sysRole = Cayenne.objectForPK(context, TSysRole.class, roleId);
                if (null == sysRole) return SystemCode.OBJECT_IS_NULL;
                // 新建一个关联关系
                TSysUserRole sysUserRole = context.newObject(TSysUserRole.class);
                // 设置关联关系对应的角色
                sysUserRole.setRole(sysRole);
                // 将关联关系加到角色对象中
                sysUser.addToUserRole(sysUserRole);
            }
            sysUser.setCtor(currUser.getId());
            sysUser.setCtime(new Date());
            // 整体提交
            context.commitChanges();

            sysLogService.saveSysLog("新建信息" + sysUser.getId(), TSysLog.INSERT, SyslogEnum.USER_CODE, currUser.getId(), context);
            String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";//
            String token = "430765a9bfb6177ea8627bd9c280a975";
            String appId = "2c046def4a4443d795ac8f96094798e8";//应用Id
            String templateId = "138957";//维修模板Id：26941；取消维修模板Id:29015
            UcPaasUtil.templateSMS(accountSid, token, appId, templateId, userPhone, userAccount + ", " + password);//发送短信

            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            if (null != context) context.rollbackChanges();
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    public SystemCode updateUser(Integer userId, String userName, String userPhone, Integer companyId, List<Integer> roleIdList, TSysUser currUser) {
        logger.info("userId:" + userId + ", userName:" + userName + ", userPhone:" + userPhone + ", companyId:" + companyId + ", currUser:" + currUser);
        if (null == userId || userId <= 0 || StringUtil.isEmpty(userName) || StringUtil.isEmpty(userPhone) || null == companyId || companyId <= 0 || null == currUser) return SystemCode.PARAMETER_ERROR;
        ObjectContext context = null;
        try {
            context = currUser.getObjectContext();
            // 新建一个子类content，这一步不是必须的，直接使用父累context提交出现异常时要进行本地回滚
            // 缓存中查出对象
            TSysUser sysUser = Cayenne.objectForPK(context, TSysUser.class, userId);
            if (null == sysUser) return SystemCode.OBJECT_IS_NULL;
            sysUser.setUserName(userName);
            sysUser.setUserPhone(userPhone);
            // 查出(缓存)id对应的公司对象
            TMtCompany mtCompany = Cayenne.objectForPK(context, TMtCompany.class, companyId);
            if (null == mtCompany) return SystemCode.OBJECT_IS_NULL;
            // 设置用户所属公司
            sysUser.setCompany(mtCompany);
            // 查出(缓存)id对应的角色名称
            List userRoleList = sysUser.getUserRole();
            if (null != userRoleList) context.deleteObjects(userRoleList);
            for (Integer roleId : roleIdList) {
                TSysRole sysRole = Cayenne.objectForPK(context, TSysRole.class, roleId);
                if (null == sysRole) return SystemCode.OBJECT_IS_NULL;
                // 新建一个关联关系
                TSysUserRole sysUserRole = context.newObject(TSysUserRole.class);
                // 设置关联关系对应的角色
                sysUserRole.setRole(sysRole);
                // 将关联关系加到角色对象中
                sysUser.addToUserRole(sysUserRole);
            }
            sysUser.setUtor(currUser.getId());
            sysUser.setUtime(new Date());
            // 整体提交
            context.commitChanges();
            sysLogService.saveSysLog("编辑信息" + sysUser.getId(), TSysLog.INSERT, SyslogEnum.USER_CODE, currUser.getId(), context);
            return SystemCode.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.CONTEXT_COMMIT_FAIL;
        }
    }

    @Override
    public List<TSysUser> findSysUsersByRoleLevelForCompany(ObjectContext context, String userName, Integer roleLevel, Integer companyId) {
        return sysUserDao.findSysUsersByRoleLevelForCompany(context,userName,roleLevel,companyId);
    }

	@Override
	public String findSysUsersByRoleLevelForCompany(ObjectContext context,
			Integer roleLevel, Integer companyId,Integer defaultUserId) {
		StringBuffer buffer = new StringBuffer("<option>请选择</option>");
		try {
		 List<TSysUser> list = sysUserDao.findSysUsersByRoleLevelForCompany(context,null,roleLevel,companyId);
		 if(list != null && list.size()>0){
        	for (TSysUser user : list) {
				if(defaultUserId != null){
					if(defaultUserId == user.getId()){
						buffer.append("<option value='"+user.getId()+"' selected>"+user.getUserName()+"</option>");
					}else{
						buffer.append("<option value='"+user.getId()+"'>"+user.getUserName()+"</option>");
					}
				}else{
					buffer.append("<option value='"+user.getId()+"'>"+user.getUserName()+"</option>");
				}
			}
          }
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return buffer.toString();
	}

	@Override
	public String findSysUsersByCompany(TMtCompany company) {
		StringBuffer buffer = new StringBuffer("<option>请选择</option>");
		try {
			 List<TSysUser> list = company.getUser();
			 if(list != null && list.size()>0){
		        	for (TSysUser user : list) {
						buffer.append("<option value='"+user.getId()+"'>"+user.getUserName()+"</option>");
					}
		          }
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return buffer.toString();
	}

	@Override
	public void flashRoleCache(TSysUser user) {
		sysUserDao.flashRoleCache(user);

	}

    @Override
    public boolean resetPassword(Integer ids, TSysUser currUser) {
        logger.info("ids:" + ids + ", currUser:" + currUser);
        try {
            TSysUser user = Cayenne.objectForPK(currUser.getObjectContext(), TSysUser.class, ids);
            user.setUtime(new Date());
            user.setUtor(currUser.getId());
            user.setLoginTime(null);
            String password = RandomStringUtils.random(6, false, true);
            user.setUserPassword(Md5Util.md5(password));
            currUser.getObjectContext().commitChanges();

            String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";//
            String token = "430765a9bfb6177ea8627bd9c280a975";
            String appId = "2c046def4a4443d795ac8f96094798e8";//应用Id
            String templateId = "138957";//维修模板Id：26941；取消维修模板Id:29015
            UcPaasUtil.templateSMS(accountSid, token, appId, templateId, user.getUserPhone(), user.getUserAccount() + ", " + password);//发送短信
            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}
