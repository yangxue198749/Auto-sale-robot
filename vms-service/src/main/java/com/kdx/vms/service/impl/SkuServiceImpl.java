package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuImg;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtSkuDao;
import com.kdx.vms.service.ISkuService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SkuServiceImpl implements ISkuService {


	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Resource
	private MtSkuDao msd;

	@Override
	public Map<String, Object> list(ObjectContext context,String name, String skuCode, Integer skuTypePid, Integer skuTypeId, Integer pageSize, Integer pageNo) {

		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("url","sku/list");
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		try {
			List<Object> list = msd.findSkuList(name, skuCode, skuTypePid, skuTypeId, pageSize, context);
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
	public Integer createSku(ObjectContext context,TMtSku tMtSku ,Integer skuTypeId ,Integer userId) {
		try {
			context.registerNewObject(tMtSku);
			tMtSku.setCtime(new Date());
			tMtSku.setCtor(userId);
			tMtSku.setSkuType(Cayenne.objectForPK(context, TMtSkuType.class ,skuTypeId));
			context.commitChanges();
			return tMtSku.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Integer updateSku(ObjectContext context,TMtSku oldTMtSku, Integer skuTypeId, Integer userId) {
		try {
			context.rollbackChanges();
			ObjectContext objectContext = ObjectContextFactory.getInstance().getChildObjectContext(context);
			TMtSku newTMtSku = Cayenne.objectForPK(objectContext, TMtSku.class, oldTMtSku.getId());
			newTMtSku.setTMtSku(oldTMtSku);
			newTMtSku.setSkuType(Cayenne.objectForPK(objectContext, TMtSkuType.class ,skuTypeId));
			newTMtSku.setUtime(new Date());
			newTMtSku.setUtor(userId);
			newTMtSku.setValidityMonitor(oldTMtSku.getValidityMonitor());
			newTMtSku.setValidityType(oldTMtSku.getValidityType());
			newTMtSku.setGeneralizeType(oldTMtSku.getGeneralizeType());
			objectContext.commitChanges();
			return newTMtSku.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public TMtSku getTMtSku(ObjectContext context,Integer skuId) {
		try {
			return Cayenne.objectForPK(context, TMtSku.class, skuId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Boolean changeSkuNew(ObjectContext context, Integer id, String imgUrl, Integer index, Integer source, Integer detail) {
		try {
			TMtSkuImg tMtSkuImg = findBySkuId(context, id, index, source);
            TMtSku sku = Cayenne.objectForPK(context,TMtSku.class,id);
			if(null != tMtSkuImg){
				tMtSkuImg.setImgUrl(imgUrl);
			}else{
				TMtSkuImg skuImg = context.newObject(TMtSkuImg.class);
				skuImg.setImgUrl(imgUrl);
				skuImg.setSku(sku);
				skuImg.setImgIndex(index);
				skuImg.setDetailDiagram(detail);
				skuImg.setSource(source);
			}
            if(source == TMtSkuImg.IMG_SOURCE_VMS && index == TMtSkuImg.IMG_FIRST_URL){
                sku.setImgUrl(imgUrl);
            }
            context.commitChanges();
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			context.rollbackChanges();
			return false;
		}
	}

	@Override
	public TMtSkuImg findBySkuId(ObjectContext context, Integer id, Integer index, Integer source) {
		try {
			Expression qualifier = ExpressionFactory.matchExp(TMtSkuImg.SKU_PROPERTY, id);
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuImg.IMG_INDEX_PROPERTY, index));
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuImg.SOURCE_PROPERTY, source));
			SelectQuery select = new SelectQuery(TMtSkuImg.class, qualifier);
			List<TMtSkuImg> skuImgs = context.performQuery(select);
			if(null != skuImgs && skuImgs.size()>0){
				return skuImgs.get(0);
			}
		}catch (Exception e){
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Integer updateSkuDescribe(ObjectContext context, Integer id, String describe ,Integer userId) {
		try {
			TMtSku tMtSku = Cayenne.objectForPK(context, TMtSku.class, id);
			tMtSku.setSkuDesc(describe);
			tMtSku.setUtime(new Date());
			tMtSku.setUtor(userId);
			context.commitChanges();
			return tMtSku.getId();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	public List<TMtSkuImg> findSkuImgsBySkuId(ObjectContext context, Integer skuId) {
		try {
			Expression expression = ExpressionFactory.matchExp(TMtSkuImg.SKU_PROPERTY, skuId);
			SelectQuery selectQuery = new SelectQuery(TMtSkuImg.class, expression);
			List<TMtSkuImg> skuImgs = context.performQuery(selectQuery);
			if(null != skuImgs && skuImgs.size() >0){
				return skuImgs;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return Collections.emptyList();
	}
}
