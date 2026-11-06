package com.kdx.vms.service;

import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TSysTypeInfo;

public interface ISysTypeInfoService {
	
	/**
	 * 根据code找到字典表对应数据
	 * @param sysTypeinfoEnum
	 * @param context
	 * @return TSysTypeinfo
	 * @date 2016年12月6日 下午6:45:06
	 * @author chenifan
	 */
	public TSysTypeInfo findSysTypeinfo(SysTypeinfoEnum sysTypeinfoEnum ,ObjectContext context );
	
	/**
	 * @see #findSysTypeinfo(SysTypeinfoEnum, ObjectContext)
	 */
	public TSysTypeInfo findSysTypeinfo(String code ,ObjectContext context );
	
	/**
	 * 查询列表
	 * @param name
	 * @param pageSize
	 * @param pageNo
	 * @param context
	 */
	public Map<String, Object> list(String name ,Integer pageSize ,Integer pageNo ,ObjectContext context);
	
	/**
	 * 新建
	 * @param tSysTypeInfo
	 * @param userId
	 * @param context
	 */
	public Integer addSysTypeinfo(TSysTypeInfo tSysTypeInfo ,ObjectContext context);
	
	/**
	 * 修改
	 * @param tSysTypeInfo
	 * @param context
	 * @return Boolean
	 */
	public Integer updateSysTypeinfo(TSysTypeInfo tSysTypeInfo ,ObjectContext context);
	
	/**
	 * 删除
	 * @param tSysTypeInfoId
	 * @param context
	 * @return Boolean
	 */
	public Boolean deleteSysTypeinfo(String[] tSysTypeInfoIds ,ObjectContext context);
	
	/**
	 * 根据id得到TSysTypeInfo
	 * @param tSysTypeInfoId
	 * @param context
	 * @return Boolean
	 */
	public TSysTypeInfo getTSysTypeInfoById(Integer id ,ObjectContext context);
}
