package com.kdx.vms.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtBusinessDistrictDao;
import com.kdx.vms.service.IMtBusinessDistrictService;

@Service
public class MtBusinessDistrictServiceImpl implements IMtBusinessDistrictService {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Resource
	private MtBusinessDistrictDao mtBusinessDistrictDao;

	@Override
	public Integer createBusinessDistrict(String name, TSysUser user, ObjectContext context) {
		try {
			TMtBusinessDistrict tMtBusinessDistrict = context.newObject(TMtBusinessDistrict.class);
			tMtBusinessDistrict.setCtime(new Date());
			tMtBusinessDistrict.setCtor(user.getId());
			tMtBusinessDistrict.setBusinessDistrictName(name);
			context.commitChanges();
			return tMtBusinessDistrict.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Integer updateBusinessDistrict(String name, String id, TSysUser user, ObjectContext context) {
		try {
			TMtBusinessDistrict tMtBusinessDistrict = Cayenne.objectForPK(context, TMtBusinessDistrict.class,id);
			if(tMtBusinessDistrict == null) return null;
			tMtBusinessDistrict.setBusinessDistrictName(name);
			tMtBusinessDistrict.setUtime(new Date());
			tMtBusinessDistrict.setUtor(user.getId());
			context.commitChanges();
			return tMtBusinessDistrict.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Boolean deleteBusinessDistrict(String[] ids, TSysUser user, ObjectContext context) {
		try {
			for (String id : ids) {
				TMtBusinessDistrict tMtBusinessDistrict = Cayenne.objectForPK(context, TMtBusinessDistrict.class,id);
				if(tMtBusinessDistrict == null) return false;
				context.deleteObject(tMtBusinessDistrict);
			}
			context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return false;
	}

	@Override
	public Map<String, Object> list(Integer pageSize, Integer pageNo, TSysUser user, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "businessDistrict/list");
		try {
			List<Object> list = mtBusinessDistrictDao.findBusinessDistrict(pageSize, context);
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
	public List<TMtBusinessDistrict> allList(ObjectContext context) {
		return mtBusinessDistrictDao.findAllBusinessDistrict(context);
	}

}
