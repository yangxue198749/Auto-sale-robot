package com.kdx.vms.service;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtArea;

/**
 * 区域接口
 * @author zhangguodong
 *
 */
public interface IMtAreaService {

	/**
	 * 根据级别查询区域信息
	 * @param context
	 * @param level 区域级别  2省，3市，4区
	 * @return
	 */
	List<TMtArea> findMtAreaByLevel(ObjectContext context,Integer level);
	
	/**
	 * 根据父级标识查询所有子级
	 * @param context
	 * @param parentId 父级标识
	 * @return
	 */
	List<TMtArea> findMtAreaByParentId(ObjectContext context,Integer parentId);
	
	/**
	 * 根据区域主键查询
	 * @param context
	 * @param areaId 区域主键
	 * @return
	 */
	TMtArea findByAreaId(ObjectContext context,Integer areaId);
	
}
