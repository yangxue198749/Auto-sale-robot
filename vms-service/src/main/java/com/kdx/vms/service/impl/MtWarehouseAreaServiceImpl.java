package com.kdx.vms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TMtWarehouseArea;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtWarehouseAreaDao;
import com.kdx.vms.service.IMtWarehouseAreaService;

@Service
public class MtWarehouseAreaServiceImpl implements IMtWarehouseAreaService {
	
	private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Resource
	private MtWarehouseAreaDao mtWarehouseAreaDao;

	@Override
	public List<TMtWarehouseArea> getAllWarehouseArea() {
		try {
			return mtWarehouseAreaDao.findWarehouseAreaDao(context);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public TMtWarehouseArea getWarehouseAreaByAreaIdAndWarehouseId(Integer areaId, Integer warehouseId, ObjectContext context) {
		try {
			return mtWarehouseAreaDao.findWarehouseAreaByAreaIdAndWarehouseId(areaId, warehouseId, context);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
