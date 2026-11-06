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
import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtPartnerDao;
import com.kdx.vms.service.IMtPartnerService;

@Service
public class MtPartnerServiceImpl implements IMtPartnerService {
	
	@Resource
	private MtPartnerDao mtPartnerDao;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Override
	public Map<String, Object> list(String partnerName, String manageName, String partnerType, Integer pageSize,
			Integer pageNo ,TSysUser user ,ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "partner/list");
		try {
			List<Object> list = mtPartnerDao.findPartnerList(partnerName, manageName, partnerType, pageSize, pageNo,user, context);
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
	public Integer updatePartner(TMtPartner oldTMtPartner, Integer userId, ObjectContext context) {
		try {
			TMtPartner newTMtPartner = Cayenne.objectForPK(context, TMtPartner.class, oldTMtPartner.getId());
			newTMtPartner.setProperties(oldTMtPartner);
			newTMtPartner.setUtime(new Date());
			newTMtPartner.setUtor(userId);
			context.commitChanges();
			return newTMtPartner.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Boolean delPartner(String[] ids, ObjectContext context) {
		try {
			if(ids != null ){
				for (String id : ids) {
					TMtPartner partner =Cayenne.objectForPK(context, TMtPartner.class, id);
					if(partner != null ) context.deleteObject(partner);
				}
				context.commitChanges();
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public List<TMtPartner> getUnusedTMtPartner(TSysUser user, ObjectContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TMtPartner getTMtCustomerById(Integer id, ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TMtPartner.class, id);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Integer addPartner(TMtPartner tMtPartner, TSysUser user, ObjectContext context) {
		try {
			context.registerNewObject(tMtPartner);
			tMtPartner.setCtime(new Date());
			tMtPartner.setCtor(user.getId());
			tMtPartner.setCompany(user.getCompany());
			context.commitChanges();
			return tMtPartner.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
