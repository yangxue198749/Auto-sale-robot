package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysDictionary;

public interface ISysDictionaryService {
	
	/**
	 * 查询列表
	 * @param name
	 * @param pageSize
	 * @param pageNo
	 * @param context
	 */
	public Map<String, Object> list(Integer tSysTypeInfoId ,String name ,Integer pageSize ,Integer pageNo ,ObjectContext context);
	
	/**
	 * 新建
	 * @param tSysDictionary
	 * @param userId
	 * @param context
	 */
	public Integer addSysTypeinfo(Integer tSysTypeInfoId ,TSysDictionary tSysDictionary ,ObjectContext context);
	
	/**
	 * 修改
	 * @param tSysDictionary
	 * @param context
	 * @return Boolean
	 */
	public Integer updateSysTypeinfo(TSysDictionary tSysDictionary ,ObjectContext context);
	
	/**
	 * 删除
	 * @param tSysDictionaryIds
	 * @param context
	 * @return Boolean
	 */
	public Boolean deleteSysTypeinfo(String[] tSysDictionaryIds ,ObjectContext context);
	
	/**
	 * 根据id得到TSysDictionary
	 * @param tSysDictionaryIds
	 * @param context
	 * @return Boolean
	 */
	public TSysDictionary getTSysDictionaryById(Integer id ,ObjectContext context);
	
	public TSysDictionary getTSysDictionaryByCode(String code ,ObjectContext context);
	
	/**
	 * 根据字典主表标识查询字典详情
	 * @param typeInfoId
	 * @param context
	 * @return
	 */
	public List<TSysDictionary> findByTypeInfoId(Integer typeInfoId,ObjectContext context);

	/**
	 * 获取字典详情信息
	 * @param code 字典详情编码
	 * @param typeInfoId 字典表主键
	 * @param context
	 * @return
	 */
	public TSysDictionary getTSysDictionaryByCodeAndTypeInfoId(String code ,Integer typeInfoId,ObjectContext context);
}
