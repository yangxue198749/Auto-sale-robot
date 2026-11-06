package com.kdx.vms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtVmTextDao;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IStVmTextService;

@Service("stVmTextService")
public class StVmTextServiceImpl implements IStVmTextService{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private StVmStrategyDao stVmStrategyDao;
	@Resource
	private MtVmTextDao mtVmTextDao;
	@Override
	public List<TStVmStrategy> findStVmTextPage(ObjectContext context,
			TSysUser user, String stVmName, String text, Integer pageSize) {
		try {
			return stVmStrategyDao.findStVmTextPage(context,user,stVmName,text,pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}
	@Override
	public TStVmText findByStrategyId(ObjectContext context, Integer strategyId) {
		try {
			return mtVmTextDao.findByStrategyId(context,strategyId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}
	@Override
	public String addStVmText(ObjectContext context, String stVmName,
			String strategyDesc, String text, TSysUser user) {
		String errmsg = null;
		try {
			if(StringUtil.isEmpty(stVmName)){
				return "缺少策略名称,新建走马灯策略失败";
			}
			if(StringUtil.isEmpty(text)){
				return "缺少走马灯内容,新建走马灯策略失败";
			}
			
			TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
			stVmStrategy.setCompany(user.getCompany());
			stVmStrategy.setCtime(new Date());
			stVmStrategy.setCtor(user.getId());
			stVmStrategy.setStrategyDesc(strategyDesc);
			stVmStrategy.setStrategyName(stVmName);
			stVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_TEXT);
			
			TStVmText stVmText = context.newObject(TStVmText.class);
			stVmText.setCtime(new Date());
			stVmText.setCtor(user.getId());
			stVmText.setTextContents(text);
			stVmStrategy.setStText(stVmText);
			
			context.commitChanges();
		} catch (Exception e) {
			errmsg = "系统内部出现异常,新增走马灯策略失败";
			context.rollbackChanges();
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return errmsg;
	}
	@Override
	public String editStVmText(ObjectContext context, String stVmName,
			String strategyDesc, String text, TSysUser user, Integer strategyId) {
		String errMsg = null;
		try {
			TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
			if(null == strategy){
				return "走马灯策略不存在,修改走马灯策略失败";
			}
			if(!strategy.getStrategyType().equals(strategy.TYPE_ST_VM_TEXT)){
				return "策略类型错误,修改走马灯策略失败";
			}
			if(StringUtil.isEmpty(stVmName)){
				return "缺少策略名称,修改走马灯策略失败";
			}
			if(StringUtil.isEmpty(text)){
				return "缺少走马灯内容,修改走马灯策略失败";
			}
			strategy.setUtime(new Date());
			strategy.setUtor(user.getId());
			strategy.setStrategyName(stVmName);
			strategy.setStrategyDesc(strategyDesc);
			TStVmText stVmText = strategy.getStText();
			stVmText.setUtime(new Date());
			stVmText.setUtor(user.getId());
			stVmText.setTextContents(text);
			
			context.commitChanges();
		} catch (Exception e) {
			context.rollbackChanges();
			errMsg = "系统内部异常，修改走马灯策略失败";
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return errMsg;
	}
	@Override
	public String deleteByStrategyIds(ObjectContext objectContext,
			TSysUser sysUser, List<Integer> ids) {
		String errMsg = null;
		try {
			if(null != ids && ids.size()>0){
				for (Integer strategyId : ids) {
					TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(objectContext, strategyId);
					if(null != strategy){
						objectContext.deleteObject(strategy);
					}else{
						logger.error("删除走马灯策略失败  sysuser = "+ sysUser + "strategyId = " + strategyId + "策略不存在");
					}
				}
				objectContext.commitChanges();
			}else{
				logger.error("删除走马灯策略失败  sysuser = "+ sysUser + "ids  为空");
				return "删除走马灯策略失败";
			}
		} catch (Exception e) {
			errMsg = "系统内部异常";
			objectContext.rollbackChanges();
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return errMsg;
	}
}
