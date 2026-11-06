package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysDictionary;

public interface SysDictionaryDao {

	public List<Object> findSysDictionaryList(Integer tSysTypeInfoId ,String name ,Integer pageSize ,ObjectContext context);
	
	public TSysDictionary findSysDictionaryByCode(String code ,ObjectContext context);

	/**
	 * 根据字典主表标识查询字典详情
	 * @param typeInfoId
	 * @param context
	 * @return
	 */
	public List<TSysDictionary> findByTypeInfoId(Integer typeInfoId,
			ObjectContext context);

	/**
	 * 获取字典详情信息
	 * @param code 字典详情编码
	 * @param typeInfoId 字典表主键
	 * @param context
	 * @return
	 */
	public TSysDictionary getTSysDictionaryByCodeAndTypeInfoId(String code,
			Integer typeInfoId, ObjectContext context);
}
