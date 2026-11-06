package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtVmChannelDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liyang on 16-12-6.
 */
@Service("mtVmChannelDao")
public class MtVmChannelDaoImpl implements MtVmChannelDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public TMtVmChannel findByChannelCode(ObjectContext context, TMtVm mtVm,
                                          String channelCode) {
        Expression qualifier = ExpressionFactory.matchExp(TMtVmChannel.MT_VM_PROPERTY, mtVm);
        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmChannel.CHANNEL_CODE_PROPERTY, channelCode));
        SelectQuery select = new SelectQuery(TMtVmChannel.class, qualifier);
        List<TMtVmChannel> list = context.performQuery(select);
        if (null != list && list.size() > 0) return list.get(0);
        return null;
    }

    @Override
    public Map<String, TMtVmChannel> getMtVmChannelInfoMap(TMtVm mtVm) {
        Map<String, TMtVmChannel> mtVmChannelInfoMap;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVmChannel.MT_VM_PROPERTY, mtVm);
            SelectQuery select = new SelectQuery(TMtVmChannel.class, qualifier);
            select.addOrdering(TMtVmChannel.CHANNEL_CODE_PROPERTY, SortOrder.ASCENDING);
            List<TMtVmChannel> list = mtVm.getObjectContext().performQuery(select);
            if (null == list)
                return null;
            mtVmChannelInfoMap = list.stream().collect(Collectors.toMap(TMtVmChannel::getChannelCode, tMtVmChannel -> tMtVmChannel));
        } catch (Exception e) {
            logger.error("select error:" + e.getLocalizedMessage(), e);
            return null;
        }
        return mtVmChannelInfoMap;
    }

    @Override
    public Map<String, TMtVmChannelNext> getMtVmChannelNextMap(TMtVm mtVm) {
        Map<String, TMtVmChannelNext> mtVmChannelNextMap;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVmChannel.MT_VM_PROPERTY, mtVm);
            SelectQuery select = new SelectQuery(TMtVmChannelNext.class, qualifier);
            select.addOrdering(TMtVmChannelNext.CHANNEL_CODE_PROPERTY, SortOrder.ASCENDING);
            List<TMtVmChannelNext> list = mtVm.getObjectContext().performQuery(select);
            if (null == list)
                return null;
            mtVmChannelNextMap = list.stream().collect(Collectors.toMap(TMtVmChannelNext::getChannelCode, tMtVmChannelNext -> tMtVmChannelNext));
        } catch (Exception e) {
            logger.error("select error:" + e.getLocalizedMessage(), e);
            return null;
        }
        return mtVmChannelNextMap;
    }


    @Override
    public List queryValidityByEnd(TSysUser user, Integer searchType, String searchValue,Integer pageSize, String skuCode) {
        try {
            Assert.notNull(user, "user not be null.");

            ObjectContext context = user.getObjectContext();
            Expression qualifier = ExpressionFactory.matchExp(TMtVmChannel.SKU_PROPERTY + "." + TMtSku.VALIDITY_MONITOR_PROPERTY, TMtSku.VALIDITY_YES);
            qualifier = ExpressionUtils.fillCompanyQualifier(qualifier, TMtVmChannel.MT_VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, user);
            //qualifier = qualifier.andExp(ExpressionFactory.lessExp(TMtVmChannel.MANUFACTURE_END_DATE_PROPERTY,new Date()));
            if (!StringUtil.isEmpty(searchValue)) {
                if (null != searchType) {
                    switch (searchType) {
                        case 1:
                            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmChannel.SKU_PROPERTY + "." + TMtSku.SKU_NAME_PROPERTY,"%" + searchValue + "%"));
                            break;
                        case 2: {
                            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVmChannel.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, searchValue);
                            break;
                        }
                        case 3: {
                            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmChannel.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY,"%" + searchValue + "%"));
                            break;
                        }
                    }
                }
            }
            if(!StringUtil.isEmpty(skuCode)){
                 qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmChannel.SKU_PROPERTY + "." + TMtSku.SKU_CODE_PROPERTY ,"%" + skuCode + "%"));
            }

            SelectQuery selectQuery = new SelectQuery(TMtVmChannel.class, qualifier);
            selectQuery.addOrdering(TMtVmChannel.MANUFACTURE_END_DATE_PROPERTY, SortOrder.ASCENDING);
            selectQuery.addPrefetch(TMtVmChannel.SKU_PROPERTY);
            selectQuery.addPrefetch(TMtVmChannel.SKU_PROPERTY+"."+TMtSku.SKU_IMG_PROPERTY);
            selectQuery.addPrefetch(TMtVmChannel.MT_VM_PROPERTY);
            selectQuery.addPrefetch(TMtVmChannel.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
            selectQuery.addPrefetch(TMtVmChannel.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." +TMtNode.DELIVERY_PROPERTY);
            selectQuery.setPageSize(pageSize);

            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<TMtVmChannel> findByVmId(ObjectContext context, Integer vmId) {
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVmChannel.MT_VM_PROPERTY , vmId);
            expression = expression.andExp(ExpressionFactory.noMatchExp(TMtVmChannel.SKU_PROPERTY ,null));
            SelectQuery select = new SelectQuery(TMtVmChannel.class, expression);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

}
