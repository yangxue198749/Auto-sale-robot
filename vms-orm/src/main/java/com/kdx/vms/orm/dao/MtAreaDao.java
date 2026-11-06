package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtArea;

/**
 *
 * Created by liyang on 2016/11/28.
 */
public interface MtAreaDao {

	/**
	 * 根据级别查询区域信息
	 * @param context
	 * @param level 区域级别  2省，3市，4区
	 * @return
	 */
	List<TMtArea> findMtArea(ObjectContext context, Integer level);

    List getAllArea(ObjectContext context);
}
