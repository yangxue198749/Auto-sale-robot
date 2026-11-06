package com.kdx.vms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.bean.TMtWarehouse;
import com.kdx.vms.model.bean.TMtWarehouseArea;
import com.kdx.vms.orm.dao.MtWarehouseDao;
import com.kdx.vms.service.IMtAreaService;
import com.kdx.vms.service.IMtWarehouseAreaService;
import com.kdx.vms.service.IMtWarehouseService;

@Service("mtWarehouseService")
public class MtWarehouseServiceImpl implements IMtWarehouseService {

	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Resource
	private MtWarehouseDao mtWarehouseDao;
	
	@Resource
	private IMtAreaService iMtAreaService;
	
	@Resource
	private IMtWarehouseAreaService iMtWarehouseAreaService;
	
	@Override
	public Map<String, Object> list(String idCode ,String name ,Integer pageSize ,Integer pageNo ,ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "warehouse/list");
		try {
			List<Object> list = mtWarehouseDao.findWarehouseList(idCode, name, pageSize, context);
			map.put("totalSize", list.size());
			map.put("nodeList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			 logger.error(e.getLocalizedMessage(), e);
			 map.put("totalSize", 0);
			 map.put("nodeList", null);
		}
		return map;
	}

	@Override
	public Integer uniteArea(String[] areaIds , Integer warehouseId ,Integer cityId ,int userId ,ObjectContext context) {
		try {
			TMtWarehouse tMtWarehouse = Cayenne.objectForPK(context, TMtWarehouse.class , warehouseId);
			List<TMtWarehouseArea> wareHouseAreaList = new ArrayList<TMtWarehouseArea>();
			for (TMtWarehouseArea tMtWarehouseArea : tMtWarehouse.getWareHouseArea()) {
				if(tMtWarehouseArea.getArea().getParentArea().getId().equals(cityId)){
					wareHouseAreaList.add(tMtWarehouseArea);
				}
			}
			context.deleteObjects(wareHouseAreaList);
			for (String areaId : areaIds) {
				if(StringUtils.isEmpty(areaId)) continue ;
				TMtWarehouseArea warehouseArea = context.newObject(TMtWarehouseArea.class);
				warehouseArea.setCtime(new Date());
				warehouseArea.setCtor(Long.valueOf(userId).intValue());
				warehouseArea.setWareHouse(Cayenne.objectForPK(context, TMtWarehouse.class, warehouseId));
				warehouseArea.setArea(iMtAreaService.findByAreaId(context, Integer.valueOf(areaId)));
			}
			context.commitChanges();
			return tMtWarehouse.getId();
		} catch (Exception e) {
			 logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> lookUniteArea(int warehouseId, ObjectContext context) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		TMtWarehouse tMtWarehouse = Cayenne.objectForPK(context, TMtWarehouse.class, warehouseId);
		for (TMtWarehouseArea warehouseArea : tMtWarehouse.getWareHouseArea()) {
			Map<String, Object> map = new HashMap<String, Object>();
			TMtArea area = warehouseArea.getArea();
			map.put("area", area.getAreaName());
			map.put("city", area.getParentArea().getAreaName());
			map.put("province", area.getParentArea().getParentArea().getAreaName());
			list.add(map);
		}
		return list;
	}

}
