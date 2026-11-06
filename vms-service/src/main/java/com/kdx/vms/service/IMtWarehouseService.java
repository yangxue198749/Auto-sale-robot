package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

public interface IMtWarehouseService {

	public Map<String ,Object> list(String idCode ,String name ,Integer pageSize ,Integer pageNo ,ObjectContext context);
	
	public Integer uniteArea(String[] areaIds ,Integer warehouseId ,Integer cityId ,int userId ,ObjectContext context);
	
	public  List<Map<String,Object>> lookUniteArea(int warehouseId ,ObjectContext context);
	
}
