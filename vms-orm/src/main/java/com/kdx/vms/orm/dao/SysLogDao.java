package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;

public interface SysLogDao {
	
	public List<Object> findSysLogList(List<TSysUser> userIdList, Integer pageSize, ObjectContext context);

}
