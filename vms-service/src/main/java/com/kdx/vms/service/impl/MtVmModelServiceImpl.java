package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtVmModelDao;
import com.kdx.vms.service.IMtVmModelChannelService;
import com.kdx.vms.service.IMtVmModelService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 16-11-30.
 */
@Service("mtVmModelService")
public class MtVmModelServiceImpl implements IMtVmModelService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MtVmModelDao mtVmModelDao;
    
    @Resource
    private IMtVmModelChannelService iMtVmModelChannelService;

    public List findMtVmModel(ObjectContext context, Boolean showShortcuts) {
        logger.debug("context:" + context);
        return mtVmModelDao.findMtVmModelAll(context, showShortcuts);
    }

	@Override
	public List findMtVmModelByCombinatorialPatterns(TSysUser user, Object... combinatorialPatterns) {
		logger.debug("user:" + user);
		return mtVmModelDao.findMtVmModelByCombinatorialPatterns(user, combinatorialPatterns);
	}


	@Override
	public Integer createVmModel(TMtVmModel tMtVmModel, TSysUser user, ObjectContext context) {
		try {
			tMtVmModel.setCtime(new Date());
			tMtVmModel.setCtor(user.getId());
			tMtVmModel.setDeleteMark(TMtVmModel.DELETE_DEFAULT_MARK);

			StringBuilder sb = new StringBuilder();
			for (String num : tMtVmModel.getChannelPerLayer().split(",")) {
				sb.append(num.trim()).append(",");
			}
			tMtVmModel.setChannelPerLayer(sb.length() > 0?sb.substring(0,sb.length()-1):"");
			context.registerNewObject(tMtVmModel);
			iMtVmModelChannelService.createModelChannel(tMtVmModel, user, context);
			context.commitChanges();
			return tMtVmModel.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Integer updateVmModel(TMtVmModel tMtVmModel, TSysUser user, ObjectContext context) {
		try {
			TMtVmModel newVmModel = Cayenne.objectForPK(context, TMtVmModel.class ,tMtVmModel.getId());
			if(newVmModel == null ) return null;
			newVmModel.copyTMtVmModel(tMtVmModel);
			newVmModel.setUtor(user.getId());
			context.commitChanges();
			return newVmModel.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public Boolean deleteVmModel(String id, ObjectContext context) {
		try {
			TMtVmModel tMtVmModel = Cayenne.objectForPK(context, TMtVmModel.class ,id);
			if(tMtVmModel == null ) return false;
			List<TMtVmModelChannel> vmModelChannelList = tMtVmModel.getModelChannels();
			if(vmModelChannelList != null) context.deleteObjects(vmModelChannelList);
			context.deleteObject(tMtVmModel);
			context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return false;
	}

	@Override
	public Map<String, Object> list(String name, Integer pageSize, Integer pageNo, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "vmModel/list");
		try {
			List<Object> list = mtVmModelDao.findVmModel(name, pageSize, context);
			map.put("totalSize", list.size());
			List<Object> subList = PageUtil.formatList(pageNo, pageSize, list);
			map.put("nodeList", subList);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			map.put("totalSize", 0);
			map.put("nodeList", null);
		}
		return map;
	}

	@Override
	public TMtVmModel getTMtVmModel(String id ,ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TMtVmModel.class , id);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String,Object> lockedOrUnlocked(String id,ObjectContext context){
		Map<String,Object> maps=new HashedMap();
		try{
			TMtVmModel mtVmModel = this.getTMtVmModel(id, context);
			if(null == mtVmModel.getDeleteMark() || 1 == mtVmModel.getDeleteMark()){
				mtVmModel.setDeleteMark(0);//0是启用
				maps.put("message","启用成功");
			}else{
				mtVmModel.setDeleteMark(1);//1是禁用
				maps.put("message","禁用成功");
			}
			context.commitChanges();
			maps.put("type","success");
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			context.rollbackChanges();
			maps.put("type","error");
			maps.put("message","失败");
		}
		return maps;
	}

}
