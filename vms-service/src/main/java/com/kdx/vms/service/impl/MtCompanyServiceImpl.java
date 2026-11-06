package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtCompanyDao;
import com.kdx.vms.service.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.Transaction;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MtCompanyServiceImpl implements IMtCompanyService {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	private final static String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";
	private final static String authToken = "430765a9bfb6177ea8627bd9c280a975";
	private final static String appId = "2c046def4a4443d795ac8f96094798e8";
	private final static String templateId = "138957";
	
	
	@Resource
	private MtCompanyDao mtCompanyDao;
	
	@Resource
	private IMtVmService iMtVmService;
	
	@Resource
	private ISysUserService iSysUserService;
	
	@Resource
	private ISysLogService iSysLogService;
	
	@Resource(name= "sysRoleService")
	private ISysRoleService iSysRoleService;

	@Override
	public Map<String, Object> addCompany(String companyName, String customerId, String adminName, String adminAccount, String userPhone, Integer maxVmCapacity, TSysUser user, ObjectContext context) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", false);
		Transaction tx = ObjectContextFactory.getInstance().getTransaction();
		try {
			Transaction.bindThreadTransaction(tx);
			Date date = new Date();
			int userId = user.getId(); 
			TMtCompany tMtCompany = context.newObject(TMtCompany.class);
			tMtCompany.setCompanyName(companyName);
			tMtCompany.setCompanyLevel(TMtCompany.LEVEL_THREE);
			context.commitChanges();
			
			TMtCompany company = user.getCompany();
			if(StringUtils.isNotEmpty(customerId) && company.getCompanyLevel() == TMtCompany.LEVEL_ONE){//说明是1级公司创建公司
				tMtCompany.setCustomer(Cayenne.objectForPK(context, TMtCustomer.class, customerId));
			}else if(StringUtils.isEmpty(customerId)  && company.getCompanyLevel() == TMtCompany.LEVEL_TWO){//是2级公司创建公司,关联2级公司的客户
				tMtCompany.setCustomer(company.getCustomer());
				if(maxVmCapacity > company.getMaxVmCapacity()){
					map.put("message", "失败:售货机数量不能超过当前未使用售货机数量。");
					return map;
				}else{
				}
			}else{
				map.put("message", "失败:三级公司不能创建子公司。");
				return map;
			}
			company.setMaxVmCapacity(company.getMaxVmCapacity() - maxVmCapacity);
			company.addToChildCompany(tMtCompany);
			
			tMtCompany.setMaxVmCapacity(maxVmCapacity);
			tMtCompany.setCtime(date);
			tMtCompany.setCtor(userId);
			
			TSysUser tSysUser = context.newObject(TSysUser.class);
			tSysUser.setCompany(tMtCompany);
			tSysUser.setCtime(date);
			tSysUser.setCtor(userId);
			tSysUser.setDeleteMark(0);
			tSysUser.setFailCount(0);
			tSysUser.setLockMark(0);
			
			if( iSysUserService.getUserByAccount(context, adminAccount) != null){
				map.put("message", "失败:此登录账号已被注册。");
				return map;
			}
			String password = RandomStringUtils.random(6,false,true);
			UcPaasUtil.templateSMS(accountSid, authToken, appId, templateId, userPhone, adminAccount+","+password);//发送短信
			
			tSysUser.setUserPassword(Md5Util.md5(password));
			tSysUser.setUserName(adminName);
			tSysUser.setUserAccount(adminAccount);
			tSysUser.setUserPhone(userPhone);
			
			TSysUserRole tSysUserRole = context.newObject(TSysUserRole.class);
			tSysUserRole.setCtime(date);
			tSysUserRole.setCtor(userId);

            // 创建管理员角色
//            TSysRole sysRole = context.newObject(TSysRole.class);
//            sysRole.setCtor(userId);
//            sysRole.setCtime(date);
//            sysRole.setRoleLevel(TSysRole.ROLE_LEVEL_ADMIN);
//            sysRole.setCompany(tMtCompany);
//            sysRole.setRoleType(TSysRole.ROLE_TYPE_COMPANY);
            
            // 管理员角色加入中间表
            tSysUserRole.setRole(iSysRoleService.getAdminRole(context, TSysRole.ROLE_LEVEL_ADMIN_3));
			tSysUserRole.setUser(tSysUser);
			context.commitChanges();
			iSysLogService.saveSysLog("新建公司"+tMtCompany.getId(), TSysLog.INSERT, SyslogEnum.COMPANY_CODE, userId, context);
			tx.commit();
			map.put("state", true);
			map.put("message", "创建成功");
			return map;
		} catch (Exception e) {
			tx.setRollbackOnly();
			logger.error(e.getLocalizedMessage(),e);
		}finally {
		    Transaction.bindThreadTransaction(null);
		    
		    if (tx.getStatus() == Transaction.STATUS_MARKED_ROLLEDBACK) {
		        try {
		           tx.rollback();
		        }
		        catch (Exception rollbackEx) {
		        }
		    }
		}
		map.put("message", "失败:提交异常");
		return map;
	}

	@Override
	public Map<String, Object> upgradeCompany(String companyName, String newAdminUserId, TSysUser user, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", false);
		Transaction tx = ObjectContextFactory.getInstance().getTransaction();
		try {
			Transaction.bindThreadTransaction(tx);
			TMtCompany threeCompany = user.getCompany();
			if(threeCompany.getCompanyLevel() != TMtCompany.LEVEL_THREE || threeCompany.getParentCompany().getCompanyLevel() != TMtCompany.LEVEL_ONE){
				map.put("message", "失败:二级公司不能升级。");
				return map;
			}
			Date date = new Date();
			TMtCompany oneCompany = threeCompany.getParentCompany();
			
			//创建二级公司
			TMtCompany twoCompany = context.newObject(TMtCompany.class);
			twoCompany.setCompanyLevel(TMtCompany.LEVEL_TWO);
			twoCompany.setCompanyName(companyName);
			twoCompany.setCtime(date);
			twoCompany.setCtor(user.getId());
			twoCompany.setCustomer(threeCompany.getCustomer());
			context.commitChanges();
			//end
			
			//查询售货机数量
			List<TMtVm> tMtVmList = iMtVmService.findMtVmList(context, null, null, null, null, null, null, null, null, user, threeCompany.getMaxVmCapacity(), false);
			
			//新公司（二级公司）和原公司（三级公司）和一级公司关联。售货机重新分配
			twoCompany.setMaxVmCapacity(threeCompany.getMaxVmCapacity() - tMtVmList.size());
			twoCompany.addToChildCompany(threeCompany);
			twoCompany.setParentCompany(oneCompany);
			threeCompany.setMaxVmCapacity(tMtVmList.size());
			
			//原管理员user移除与三级公司的关联
			TSysRole threeSysRole = iSysRoleService.getAdminRole(context, TSysRole.ROLE_LEVEL_ADMIN_3);
			List<TSysUserRole> oldUserRoleList = user.getUserRole();
			for (TSysUserRole tSysUserRole : oldUserRoleList) {
				TSysRole role = tSysUserRole.getRole();
				if(role.getId().equals(threeSysRole.getId())){
					context.deleteObject(tSysUserRole);//三级公司删除管理员
					break;
				}
			}
			//end
			
			//原公司（三级公司）与新管理员关联
			TSysUser newAdminUser = Cayenne.objectForPK(context, TSysUser.class, newAdminUserId);
			if(newAdminUser == null || newAdminUser.getId() == user.getId()){
				map.put("message", "必须指定一个用户为原公司管理员，且不能为自己");
				return map;
			}
			TSysUserRole newAdminUserRole = context.newObject(TSysUserRole.class);
			newAdminUserRole.setRole(iSysRoleService.getAdminRole(context, TSysRole.ROLE_LEVEL_ADMIN_3));
			newAdminUserRole.setCtime(date);
			newAdminUserRole.setCtor(user.getId());
			newAdminUser.addToUserRole(newAdminUserRole);
			//end
			
			//升级原管理员为二级公司管理员
			user.setCompany(twoCompany);
			TSysUserRole tSysUserRole = context.newObject(TSysUserRole.class);
			tSysUserRole.setRole(iSysRoleService.getAdminRole(context, TSysRole.ROLE_LEVEL_ADMIN_2));
			tSysUserRole.setCtime(date);
			tSysUserRole.setCtor(user.getId());
			user.addToUserRole(tSysUserRole);
			
			context.commitChanges();
			iSysLogService.saveSysLog("升级公司"+threeCompany.getId()+","+twoCompany.getId(), TSysLog.UPDATE, SyslogEnum.COMPANY_CODE, user.getId(), context);
			tx.commit();
			map.put("state", true);
			map.put("message", "创建成功");
			return map;
		} catch (Exception e) {
			tx.setRollbackOnly();
			logger.error(e.getLocalizedMessage(),e);
		}finally {
		    Transaction.bindThreadTransaction(null);
		    
		    if (tx.getStatus() == Transaction.STATUS_MARKED_ROLLEDBACK) {
		        try {
		           tx.rollback();
		        }
		        catch (Exception rollbackEx) {
		        }
		    }
		}
		map.put("message", "失败:提交异常");
		return map;
	}

	@Override
	public Map<String, Object> list(String companyName, String customerName, String adminName, Integer pageSize,
			Integer pageNo, TSysUser user, ObjectContext context) {
		
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "company/list");
		try {
			List<Object> list = null ;
			TMtCompany company = user.getCompany();
			switch (company.getCompanyLevel()) {
			case TMtCompany.LEVEL_ONE://查询所有二级和三级公司，但不能查询自己
				list = mtCompanyDao.findCompanyList(companyName, customerName, adminName, TMtCompany.LEVEL_ONE, null, pageSize, context);
				break;
			case TMtCompany.LEVEL_TWO://查询自己和所属于自己的三级公司
				list = new ArrayList<Object>();
				list.add(company);
				list.addAll(mtCompanyDao.findCompanyList(companyName, customerName, adminName, null, company.getId(), pageSize, context));
				break;
			case TMtCompany.LEVEL_THREE://查询自己
				list = new ArrayList<Object>();
				list.add(company);
				break;
			}
			map.put("totalSize", list.size());
			List<Object> subList = PageUtil.formatList(pageNo, pageSize, list);
			for (Object object : subList) {
				TMtCompany tMtCompany = (TMtCompany)object;
				tMtCompany.setAdminUser(mtCompanyDao.findAdminSysUserByCompany(context, tMtCompany.getId(), tMtCompany.getCompanyLevel()));//设置管理员
			}
			map.put("nodeList", subList);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			map.put("totalSize", 0);
			map.put("nodeList", null);
		}
		return map;
	}

	@Override
	public Integer updateCompany(String companyId, String companyName, TSysUser user, ObjectContext context) {
		try {
			TMtCompany tMtCompany = Cayenne.objectForPK(context, TMtCompany.class ,companyId);
			if(tMtCompany == null) return null;
			tMtCompany.setCompanyName(companyName);
			tMtCompany.setUtor(user.getId());
			tMtCompany.setUtime(new Date());
			context.commitChanges();
			return tMtCompany.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Boolean addCapacity(String companyId, Integer maxVmCapacity, TSysUser user, ObjectContext context) {
		try {
			Date data = new Date();
			TMtCompany tMtCompany = Cayenne.objectForPK(context, TMtCompany.class ,companyId);
			if(tMtCompany == null) return false;
			tMtCompany.setMaxVmCapacity(tMtCompany.getMaxVmCapacity()+maxVmCapacity);
			tMtCompany.setUtor(user.getId());
			tMtCompany.setUtime(data);
			
			TMtCompany userCompany = user.getCompany();
			userCompany.setMaxVmCapacity(userCompany.getMaxVmCapacity() - maxVmCapacity);
			userCompany.setUtime(data);
			userCompany.setUtor(user.getId());
			context.commitChanges();
			iSysLogService.saveSysLog("为公司"+companyId+"分配售货机", TSysLog.UPDATE, SyslogEnum.COMPANY_CODE, user.getId(), context);
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public TMtCompany getTMtCompanyById(Integer companyId, ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TMtCompany.class ,companyId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String,Object> exportOrUnStock(String id,ObjectContext context){
		Map<String,Object> maps=new HashedMap();
		try{
			TMtCompany tMtCompany = this.getTMtCompanyById(Integer.valueOf(id),context);
			if(null == tMtCompany.getExportStock() || 0 == tMtCompany.getExportStock()){
				tMtCompany.setExportStock(1);//1是启动导出
				maps.put("message","启用成功");
			}else {
				tMtCompany.setExportStock(0);//0是取消导出
				maps.put("message","取消成功");
			}
			context.commitChanges();
			maps.put("type","success");
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			context.rollbackChanges();
			maps.put("type","error");
			maps.put("message","失败");
		}
		return maps;
	}
	@Override
	public List<TMtCompany> findCompanyPage(String companyName, TMtCompany company, Integer pageSize, ObjectContext context) {
		try{
			return mtCompanyDao.findCompanyPage(companyName,company,pageSize,context);
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(),e);
		}
		return Collections.emptyList();
	}

}
