package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtWarehouseArea;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtWarehouseAreaDao {
	
	public List<TMtWarehouseArea> findWarehouseAreaDao(ObjectContext context);
	
	public TMtWarehouseArea findWarehouseAreaByAreaIdAndWarehouseId(Integer areaId, Integer warehouseId, ObjectContext context);
}
