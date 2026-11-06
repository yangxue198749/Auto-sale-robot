package com.kdx.vms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.kdx.vms.orm.cache.DictCache;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.orm.dao.SysDictionaryDao;
import com.kdx.vms.service.ISysDictionaryService;

@Service
public class SysDictionaryServiceImpl implements ISysDictionaryService {

	@Resource
	private SysDictionaryDao sysDictionaryDao;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Override
	public Map<String, Object> list(Integer tSysTypeInfoId, String name, Integer pageSize, Integer pageNo,
			ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url","dictionary/list");
		try {
			List<Object> list = sysDictionaryDao.findSysDictionaryList(tSysTypeInfoId, name, pageSize, context);
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
	public Integer addSysTypeinfo(Integer tSysTypeInfoId, TSysDictionary tSysDictionary, ObjectContext context) {
		try {
			context.registerNewObject(tSysDictionary);
			tSysDictionary.setTypeInfo(Cayenne.objectForPK(context, TSysTypeInfo.class, tSysTypeInfoId));
			context.commitChanges();
			DictCache.refreshDictCache();
			return tSysDictionary.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Integer updateSysTypeinfo(TSysDictionary tSysDictionary, ObjectContext context) {
		try {
			TSysDictionary newTSysDictionary = Cayenne.objectForPK(context, TSysDictionary.class, tSysDictionary.getId());
			if(null != newTSysDictionary){
				if(StringUtils.isNotEmpty(tSysDictionary.getDictionaryDesc())) newTSysDictionary.setDictionaryDesc(tSysDictionary.getDictionaryDesc());
				if(StringUtils.isNotEmpty(tSysDictionary.getDictionaryName())) newTSysDictionary.setDictionaryName(tSysDictionary.getDictionaryName());
				if(tSysDictionary.getDictionaryState() != null) newTSysDictionary.setDictionaryState(tSysDictionary.getDictionaryState());
				context.commitChanges();
				DictCache.refreshDictCache();
				return newTSysDictionary.getId();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Boolean deleteSysTypeinfo(String[] tSysDictionaryIds, ObjectContext context) {
		try {
			for (String tSysDictionaryId : tSysDictionaryIds) {
				TSysDictionary tSysDictionary = Cayenne.objectForPK(context, TSysDictionary.class, tSysDictionaryId);
				context.deleteObject(tSysDictionary);
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
	public TSysDictionary getTSysDictionaryById(Integer id ,ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TSysDictionary.class, id);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public TSysDictionary getTSysDictionaryByCode(String code, ObjectContext context) {
		return sysDictionaryDao.findSysDictionaryByCode(code, context);
	}

	@Override
	public List<TSysDictionary> findByTypeInfoId(Integer typeInfoId,
			ObjectContext context) {
		return sysDictionaryDao.findByTypeInfoId(typeInfoId,context);
	}

	@Override
	public TSysDictionary getTSysDictionaryByCodeAndTypeInfoId(String code,
			Integer typeInfoId, ObjectContext context) {
		return sysDictionaryDao.getTSysDictionaryByCodeAndTypeInfoId(code,typeInfoId,context);
	}
}
