package com.kdx.vms.service;

import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;

public interface ISysLogService {

	/**
	 * 分页查询日志
	 * @param userName 用户名字
	 * @param pageSize
	 * @param pageNo
	 * @param context
	 * @return Map<String,Object>
	 * @date 2016年12月6日 下午6:15:06
	 * @author chenifan
	 */
	public Map<String, Object> list(TSysUser sysUser ,String userName ,Integer pageSize ,Integer pageNo ,ObjectContext context);
	
	/**
	 * 插入一条日志
	 * @param tSysLog
	 * @param userId
	 * @param context
	 * @return Boolean
	 * @date 2016年12月6日 下午6:18:55
	 * @author chenifan
	 * @QQ 995998760
	 */
	public Boolean saveSysLog(TSysLog tSysLog, Integer userId ,ObjectContext context);
	
	public Boolean saveSysLog(String logDesc ,String logOperation ,Integer moduleId, Integer userId ,ObjectContext context);
	
	public Boolean saveSysLog(String logDesc ,String logOperation ,SyslogEnum syslogEnum, Integer userId ,ObjectContext context);
}
