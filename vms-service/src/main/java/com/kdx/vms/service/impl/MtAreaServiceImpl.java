package com.kdx.vms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.orm.dao.MtAreaDao;
import com.kdx.vms.service.IMtAreaService;

@Service("mtAreaService")
public class MtAreaServiceImpl implements IMtAreaService{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private MtAreaDao mtAreaDao;
	
	@Override
	public List<TMtArea> findMtAreaByLevel(ObjectContext context, Integer level) {
		try {
			return mtAreaDao.findMtArea(context,level);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public List<TMtArea> findMtAreaByParentId(ObjectContext context,
			Integer parentId) {
		try {
			TMtArea mtArea = Cayenne.objectForPK(context, TMtArea.class, parentId);
			if(null != mtArea){
				return mtArea.getChildArea();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public TMtArea findByAreaId(ObjectContext context, Integer areaId) {
		try {
			return Cayenne.objectForPK(context, TMtArea.class, areaId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

}
