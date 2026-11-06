package com.kdx.vms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.kdx.vms.orm.cache.DictCache;
import com.kdx.vms.orm.dao.SysTypeInfoDao;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.service.ISysTypeInfoService;

@Service
public class SysTypeInfoServiceImpl implements ISysTypeInfoService {

	@Resource
	private SysTypeInfoDao sysTypeInfoDao;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	
	@Override
	public TSysTypeInfo findSysTypeinfo(SysTypeinfoEnum sysTypeinfoEnum, ObjectContext context) {
		return sysTypeInfoDao.findSysTypeinfo(sysTypeinfoEnum.getType_code(), context);
	}

	@Override
	public TSysTypeInfo findSysTypeinfo(String code, ObjectContext context) {
		return sysTypeInfoDao.findSysTypeinfo(code, context);
	}

	@Override
	public Map<String, Object> list(String name, Integer pageSize, Integer pageNo, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url","sysType/list");
		try {
			List<Object> list = sysTypeInfoDao.findTSysTypeInfoList(name, pageSize, context);
			map.put("totalSize", list.size());
			map.put("nodeList", PageUtil.formatList(pageNo, pageSize, list));
			return map;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		map.put("totalSize", 0);
		map.put("nodeList",null);
		return map;
	}

	@Override
	public Integer addSysTypeinfo(TSysTypeInfo tSysTypeInfo, ObjectContext context) {
		try {
			context.registerNewObject(tSysTypeInfo);
			context.commitChanges();
			DictCache.refreshDictCache();
			return tSysTypeInfo.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Integer updateSysTypeinfo(TSysTypeInfo tSysTypeInfo, ObjectContext context) {
		try {
			TSysTypeInfo newTSysTypeInfo = Cayenne.objectForPK(context, TSysTypeInfo.class, tSysTypeInfo.getId());
			if(null != newTSysTypeInfo){
				if(StringUtils.isNotEmpty(tSysTypeInfo.getTypeDesc())) newTSysTypeInfo.setTypeDesc(tSysTypeInfo.getTypeDesc());
				if(StringUtils.isNotEmpty(tSysTypeInfo.getTypeName())) newTSysTypeInfo.setTypeName(tSysTypeInfo.getTypeName());
				context.commitChanges();
				DictCache.refreshDictCache();
				return newTSysTypeInfo.getId();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Boolean deleteSysTypeinfo(String[] tSysTypeInfoIds, ObjectContext context) {
		try {
			for (String tSysTypeInfoId : tSysTypeInfoIds) {
				TSysTypeInfo tSysTypeInfo = Cayenne.objectForPK(context, TSysTypeInfo.class, tSysTypeInfoId);
				List<TSysDictionary> dictionaryList = tSysTypeInfo.getDictionary();
				if(dictionaryList != null && dictionaryList.size() > 0) context.deleteObjects(dictionaryList);
				context.deleteObject(tSysTypeInfo);
			}
			context.commitChanges();
			DictCache.refreshDictCache();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return false;
	}

	@Override
	public TSysTypeInfo getTSysTypeInfoById(Integer id, ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TSysTypeInfo.class, id);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}
	

}
