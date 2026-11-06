package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmSkuSort;

/**
 * 
 * Created by liyang on 2016/11/28.
 */
public interface StVmSkuSortDao {
	
	TStVmSkuSort findByPk(ObjectContext context,Integer sortId);
}
