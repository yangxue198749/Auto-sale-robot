package com.kdx.vms.service;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtWarehouseArea;

public interface IMtWarehouseAreaService {
	
	public List<TMtWarehouseArea> getAllWarehouseArea();
	
	public TMtWarehouseArea getWarehouseAreaByAreaIdAndWarehouseId(Integer areaId, Integer warehouseId, ObjectContext context);

}
