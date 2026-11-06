package com.kdx.vms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtVmModelChannelDao;
import com.kdx.vms.service.IMtVmModelChannelService;
import com.kdx.vms.service.IMtVmModelService;

@Service
public class MtVmModelChannelServiceImpl implements IMtVmModelChannelService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private MtVmModelChannelDao mtVmModelChannelDao;
	
	@Resource
	private IMtVmModelService iMtVmModelService;
	
	private static ObjectContext objectContext = ObjectContextFactory.getInstance().newObjectContext();

	@Override
	public Boolean createVmModelChannel(String modelId, List<TMtVmModelChannel> list, TSysUser user,
			ObjectContext context) {
		try {
			TMtVmModel tMtVmModel = Cayenne.objectForPK(context, TMtVmModel.class ,modelId);
			if(tMtVmModel == null ) return false;
			Date date = new Date();
			int userId = user.getId();
			for (TMtVmModelChannel tMtVmModelChannel : list) {
				context.registerNewObject(tMtVmModelChannel);
				tMtVmModelChannel.setModel(tMtVmModel);
				tMtVmModelChannel.setCtime(date);
				tMtVmModelChannel.setCtor(userId);
			}
			context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public Boolean updateVmModelChannel(String modelId, List<TMtVmModelChannel> list, TSysUser user,
			ObjectContext context) {
		try {
			Date date = new Date();
			int userId = user.getId();
			for (TMtVmModelChannel oldVmModelChannel : list) {
				TMtVmModelChannel newVmModelChannel = Cayenne.objectForPK(context, TMtVmModelChannel.class ,oldVmModelChannel.getId());
				if(oldVmModelChannel.getChannelLength() != null){
					newVmModelChannel.setChannelLength(oldVmModelChannel.getChannelLength());
				}
				if(oldVmModelChannel.getChannelHeight() != null){
					newVmModelChannel.setChannelHeight(oldVmModelChannel.getChannelHeight());
				}
				if(oldVmModelChannel.getDisplayMode() != null){
					newVmModelChannel.setDisplayMode(oldVmModelChannel.getDisplayMode());
				}
				if(oldVmModelChannel.getChannelWidth() != null){
					newVmModelChannel.setChannelWidth(oldVmModelChannel.getChannelWidth());
				}
				if(oldVmModelChannel.getAcceptWidthMax() != null){
					newVmModelChannel.setAcceptWidthMax(oldVmModelChannel.getAcceptWidthMax());
				}
				if(oldVmModelChannel.getIsFixedCapacity() != null){
					newVmModelChannel.setIsFixedCapacity(oldVmModelChannel.getIsFixedCapacity());
					newVmModelChannel.setFixedCapacity(oldVmModelChannel.getFixedCapacity());
				}
				newVmModelChannel.setUtime(date);
				newVmModelChannel.setUtor(userId);
			}
			context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public TMtVmModelChannel getVmModelChannelRowByCode(String vmModelId, String channelCode) {
		List<TMtVmModelChannel> list = mtVmModelChannelDao.findVmModelChannelRowByCode(vmModelId, channelCode, objectContext);
		if(list != null && list.size() > 0) return list.get(0);
		return null;
	}
	
	@Override
	public TMtVmModelChannel getVmModelChannelByCode(String vmModelId, String channelCode) {
		List<TMtVmModelChannel> list = mtVmModelChannelDao.findVmModelChannelByCode(vmModelId, channelCode, objectContext);
		if(list != null && list.size() > 0) return list.get(0);
		return null;
	}

	@Override
	public Integer setModelChannelRow(Integer channelCode, String vmModelId, Integer channelLength, Integer channelHeight,
			Integer displayMode, TSysUser user, ObjectContext context) {
		try {
			List<TMtVmModelChannel> list = mtVmModelChannelDao.findVmModelChannelRowByCode(vmModelId, String.valueOf(channelCode), context);
			TMtVmModel tMtVmModel = iMtVmModelService.getTMtVmModel(vmModelId, context);
			Integer colNum = Integer.valueOf(tMtVmModel.getChannelPerLayer().split(",")[channelCode -1]);//列数
			for (int i = 1; i <= colNum; i++) {
				if(list != null && list.size() != 0){//该行有部分列
					for (int j = 0; j < list.size(); j++) {
						TMtVmModelChannel tMtVmModelChannel = list.get(j);
						String code = tMtVmModelChannel.getChannelCode();
						if(code.substring(2).equals(String.valueOf(i))){//如果匹配到
							tMtVmModelChannel.setUtime(new Date());
							tMtVmModelChannel.setUtor(user.getId());
							tMtVmModelChannel.setChannelLength(channelLength);
							tMtVmModelChannel.setChannelHeight(channelHeight);
							tMtVmModelChannel.setDisplayMode(displayMode);
							tMtVmModelChannel.setModel(tMtVmModel);
							break;
						}
						if(j == list.size()-1 ){//如果没有匹配到
							createModelChannelRow(channelLength, channelHeight, displayMode, channelCode+"-"+i, tMtVmModel, user, context);
						}
					}
				}else{//完全没有
					createModelChannelRow(channelLength, channelHeight, displayMode, channelCode+"-"+i, tMtVmModel, user, context);
				}
			}
			context.commitChanges();
			return tMtVmModel.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	@Override
	public Integer setModelChannel(String channelCode, String vmModelId, Integer channelWidth, Integer acceptWidthMax,
			Integer isFixedCapacity, Integer fixedCapacity, TSysUser user, ObjectContext context) {
		try {
			List<TMtVmModelChannel> list = mtVmModelChannelDao.findVmModelChannelByCode(vmModelId, channelCode, context);
			TMtVmModel tMtVmModel = iMtVmModelService.getTMtVmModel(vmModelId, context);
			Date date = new Date();
			if(list != null && list.size() == 1){
				TMtVmModelChannel tMtVmModelChannel = list.get(0);
				tMtVmModelChannel.setUtime(date);
				tMtVmModelChannel.setUtor(user.getId());
				tMtVmModelChannel.setChannelWidth(channelWidth);
				tMtVmModelChannel.setAcceptWidthMax(acceptWidthMax);
				tMtVmModelChannel.setIsFixedCapacity(isFixedCapacity);
				tMtVmModelChannel.setModel(tMtVmModel);
				tMtVmModelChannel.setFixedCapacity(fixedCapacity);
			}else{
				TMtVmModelChannel newTMtVmModelChannel = context.newObject(TMtVmModelChannel.class);
				newTMtVmModelChannel.setCtime(date);
				newTMtVmModelChannel.setCtor(user.getId());
				newTMtVmModelChannel.setChannelWidth(channelWidth);
				newTMtVmModelChannel.setAcceptWidthMax(acceptWidthMax);
				newTMtVmModelChannel.setIsFixedCapacity(isFixedCapacity);
				newTMtVmModelChannel.setChannelCode(channelCode);
				newTMtVmModelChannel.setModel(tMtVmModel);
				newTMtVmModelChannel.setFixedCapacity(fixedCapacity);
			}
			context.commitChanges();
			return tMtVmModel.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private void createModelChannelRow(Integer channelLength, Integer channelHeight, Integer displayMode, String channelCode, TMtVmModel tMtVmModel, TSysUser user,
			ObjectContext context) {
		TMtVmModelChannel newTMtVmModelChannel = context.newObject(TMtVmModelChannel.class);
		newTMtVmModelChannel.setCtime(new Date());
		newTMtVmModelChannel.setCtor(user.getId());
		newTMtVmModelChannel.setChannelLength(channelLength);
		newTMtVmModelChannel.setChannelHeight(channelHeight);
		newTMtVmModelChannel.setDisplayMode(displayMode);
		newTMtVmModelChannel.setChannelCode(channelCode);
		newTMtVmModelChannel.setModel(tMtVmModel);
	}

	@Override
	public Boolean createModelChannel(TMtVmModel tMtVmModel, TSysUser user, ObjectContext context) {
		try {
			String[] rowColNum = tMtVmModel.getChannelPerLayer().split(",");
			for (int i = 0; i < rowColNum.length; i++) {//行
				Integer colNum = Integer.valueOf(rowColNum[i]);
				for (int j = 0; j < colNum; j++) {//列
					TMtVmModelChannel newTMtVmModelChannel = context.newObject(TMtVmModelChannel.class);
					newTMtVmModelChannel.setCtime(new Date());
					newTMtVmModelChannel.setCtor(user.getId());
					newTMtVmModelChannel.setChannelLength(0);
					newTMtVmModelChannel.setChannelHeight(0);
					newTMtVmModelChannel.setDisplayMode(0);
					newTMtVmModelChannel.setChannelWidth(0);
					newTMtVmModelChannel.setAcceptWidthMax(0);
					newTMtVmModelChannel.setIsFixedCapacity(0);
					newTMtVmModelChannel.setModel(tMtVmModel);
					newTMtVmModelChannel.setChannelCode((i+1)+"-"+(j+1));
				}
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

}
