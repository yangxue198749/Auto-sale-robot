package com.kdx.vms.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.SysLogDao;
import com.kdx.vms.service.ISysDictionaryService;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ISysUserService;

@Service
public class SysLogServiceImpl implements ISysLogService {
	
	@Resource
	private ISysUserService iSysUserService;
	
	@Resource
	private SysLogDao sysLogDao;
	
	@Resource
	private ISysDictionaryService iSysDictionaryService;

	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Override
	public Map<String, Object> list(TSysUser sysUser ,String userName, Integer pageSize, Integer pageNo, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "sysLog/list");
		try {
			List<TSysUser> userList = Collections.emptyList();
//			if(StringUtils.isNotEmpty(userName)){
//				userList = iSysUserService.getTSysUserByName(userName, context);
//			}else{
				TMtCompany company = sysUser.getCompany();
				switch (company.getCompanyLevel()) {
				case TMtCompany.LEVEL_TWO:
					userList = company.getUser();
					List<TMtCompany> childCompany = company.getChildCompany();
					for (TMtCompany tMtCompany : childCompany) {
						userList.addAll(tMtCompany.getUser());
					}
					break;
				case TMtCompany.LEVEL_THREE:
					userList = company.getUser();
					break;
				default:
					break;
				}
//			}
			if(userList.isEmpty()&&StringUtils.isNotEmpty(userName)){
				userList = iSysUserService.getTSysUserByName(userName, context);
			}else if(StringUtils.isNotEmpty(userName)){
				List<TSysUser> ulist = new ArrayList<>();
				for(TSysUser user:userList){
					if(user.getUserName().contains(userName)){
						ulist.add(user);
					}
				}
				userList = ulist;
				if(userList.isEmpty()){
					map.put("totalSize", 0);
					map.put("nodeList", null);
					return map;
				}
			}

			List<Object> list = sysLogDao.findSysLogList(userList, pageSize, context);
			
			map.put("totalSize", list.size());
			List<Object> subList = PageUtil.formatList(pageNo, pageSize, list);
			map.put("nodeList", subList);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			map.put("totalSize", 0);
			map.put("nodeList", null);
		}
		return map;
	}

	@Override
	public Boolean saveSysLog(TSysLog tSysLog, Integer userId, ObjectContext context) {
		try {
			context.registerNewObject(tSysLog);
			tSysLog.setUserId(userId);
			tSysLog.setLogTime(new Date());
			context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public Boolean saveSysLog(String logDesc, String logOperation, Integer moduleId, Integer userId,
			ObjectContext context) {
		try {
			TSysLog tSysLog = context.newObject(TSysLog.class);
			tSysLog.setLogDesc(logDesc);
			tSysLog.setLogOperation(logOperation);
			tSysLog.setModuleId(moduleId);
			tSysLog.setLogTime(new Date());
			tSysLog.setUserId(userId);
			context.commitChanges();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public Boolean saveSysLog(String logDesc, String logOperation, SyslogEnum syslogEnum, Integer userId,
			ObjectContext context) {
		try {
			TSysDictionary tSysDictionary = iSysDictionaryService.getTSysDictionaryByCode(syslogEnum.getType_code(), context);
			if(tSysDictionary != null) return this.saveSysLog(logDesc, logOperation, tSysDictionary.getId(), userId, context);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

}
