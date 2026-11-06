package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtCustomerDao;
import com.kdx.vms.service.IMtCustomerService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MtCustomerServiceImpl implements IMtCustomerService {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Resource
	private MtCustomerDao mcd;
	
	@Override
	public Integer addCustomer(TMtCustomer tMtCustomer ,Integer userId ,ObjectContext context) {
		try {
			context.registerNewObject(tMtCustomer);
			tMtCustomer.setCtime(new Date());
			tMtCustomer.setCtor(userId);
			context.commitChanges();

//			TErpDataSync erpDataSync = context.newObject(TErpDataSync.class);
//			erpDataSync.setCtime(new Date());
//			erpDataSync.setCtor(userId);
//			erpDataSync.setDataContent(CustomerObject.getInstance(tMtCustomer,TErpDataSync.OPERATE_ADD).getJson());
//			erpDataSync.setSyncUrl(ErpConfig.URL_PUSH_CUSTOMER);
//			erpDataSync.setDataType("客户信息");
//			erpDataSync.setSyncMark(TErpDataSync.SYNC_FALSE);
			context.commitChanges();
//			PushSingleThread pushSingleThread = new PushSingleThread(context,erpDataSync);
//			Thread thread = new Thread(pushSingleThread);
//			thread.start();

			return tMtCustomer.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Object> list(String customerName, String manageName, String customerMode, String customerType,
			Integer pageSize, Integer pageNo,TSysUser user ,ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url", "customer/list");
		try {
			List<Object> list = mcd.findCustomerList(customerName, manageName, customerMode, customerType, pageSize, context);
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
	public Integer updateCustomer(TMtCustomer oldTMtCustomer, Integer userId, ObjectContext context) {
		try {
			TMtCustomer newTMtCustomer = Cayenne.objectForPK(context, TMtCustomer.class, oldTMtCustomer.getId());
			newTMtCustomer.setProperties(oldTMtCustomer);
			newTMtCustomer.setUtime(new Date());
			newTMtCustomer.setUtor(userId);
			context.commitChanges();

//			TErpDataSync erpDataSync = context.newObject(TErpDataSync.class);
//			erpDataSync.setCtime(new Date());
//			erpDataSync.setCtor(userId);
//			erpDataSync.setDataContent(CustomerObject.getInstance(newTMtCustomer,TErpDataSync.OPERATE_UPDATE).getJson());
//			erpDataSync.setSyncUrl(ErpConfig.URL_PUSH_CUSTOMER);
//			erpDataSync.setDataType("客户信息");
//			erpDataSync.setSyncMark(TErpDataSync.SYNC_FALSE);
			context.commitChanges();
//			PushSingleThread pushSingleThread = new PushSingleThread(context,erpDataSync);
//			Thread thread = new Thread(pushSingleThread);
//			thread.start();

			return newTMtCustomer.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Boolean delCustomer(String[] ids ,ObjectContext context) {
//		TErpDataSync[] erpDataSyncs = new TErpDataSync[ids.length];
		int i = 0;
		try {
			if(ids != null ){
				for (String id : ids) {
					TMtCustomer customer = (TMtCustomer) Cayenne.objectForPK(context, TMtCustomer.class, id);
					if(customer != null ) context.deleteObject(customer);
//					erpDataSyncs[i] = context.newObject(TErpDataSync.class);
//					erpDataSyncs[i].setCtime(new Date());
//					erpDataSyncs[i].setDataContent(CustomerObject.getInstance(customer,TErpDataSync.OPERATE_DELETE).getJson());
//					erpDataSyncs[i].setSyncUrl(ErpConfig.URL_PUSH_CUSTOMER);
//					erpDataSyncs[i].setDataType("客户信息");
//					erpDataSyncs[i].setSyncMark(TErpDataSync.SYNC_FALSE);
					i++;
				}
				context.commitChanges();
//				PushSingleThread pushSingleThread = new PushSingleThread(context,erpDataSyncs);
//				Thread thread = new Thread(pushSingleThread);
//				thread.start();
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
	public List<TMtCustomer> getUnusedTMtCustomer(TSysUser user, ObjectContext context) {
		//这里要判断下权限
		try {
			if(user.getCompany().getCompanyLevel() == TMtCompany.LEVEL_ONE){
				return mcd.findUnusedCustomer(context);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public TMtCustomer getTMtCustomerById(Integer id, ObjectContext context) {
		try {
			return Cayenne.objectForPK(context, TMtCustomer.class, id);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
}
