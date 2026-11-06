package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysTypeInfo;


public interface SysTypeInfoDao {
	
	public TSysTypeInfo findSysTypeinfo(String code ,ObjectContext context);
	
	public List<Object> findTSysTypeInfoList(String name, Integer pageSize, ObjectContext context);

}
