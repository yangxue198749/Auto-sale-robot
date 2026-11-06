package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.orm.dao.MtSkuDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MtSkuDaoImpl implements MtSkuDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> findSkuList(String name, String skuCode, Integer skuTypePid, Integer skuTypeId, Integer pageSize,
                                    ObjectContext context) {
        if (CheckUtils.check(skuCode)) {
            List<Object> list = new ArrayList<>();
            Integer skuId = Integer.parseInt(skuCode);
            if (CheckUtils.check(skuId)) {
                TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, skuId);
                if (CheckUtils.check(sku)) {
                    list.add(sku);
                }
            }
            return list;
        }


        SelectQuery query = new SelectQuery(TMtSku.class);
        if (StringUtils.isNotEmpty(name)) {
            query.andQualifier(ExpressionFactory.likeExp(TMtSku.SKU_NAME_PROPERTY, "%" + name + "%"));
        }
        if (StringUtils.isNotEmpty(skuCode)) {
            query.andQualifier(ExpressionFactory.likeExp(TMtSku.SKU_CODE_PROPERTY, "%" + skuCode + "%"));
        }
        if (skuTypePid != null) {
            query.andQualifier(ExpressionFactory.matchExp(TMtSku.SKU_TYPE_PROPERTY + "." + TMtSkuType.PARENT_PROPERTY, skuTypePid));
        }
        if (skuTypeId != null) {
            query.andQualifier(ExpressionFactory.matchExp(TMtSku.SKU_TYPE_PROPERTY, skuTypeId));
        }
        query.setPageSize(pageSize);
        query.addPrefetch(TMtSku.SKU_TYPE_PROPERTY);
        query.addPrefetch(TMtSku.SKU_TYPE_PROPERTY + "." + TMtSkuType.PARENT_PROPERTY);
        query.addOrdering(TMtSku.CTIME_PROPERTY, SortOrder.DESCENDING);
        return context.performQuery(query);
    }

    @Override
    public TMtSku findByPk(ObjectContext context, Integer skuId) {
        try {
            ObjectId id = new ObjectId(TMtSku.class.getSimpleName(), TMtSku.SKU_ID_PK_COLUMN, skuId);
            ObjectIdQuery query = new ObjectIdQuery(id);
            return (TMtSku) Cayenne.objectForQuery(context, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TMtSku findByCode(ObjectContext context, String skuCode) {
        if (StringUtil.isEmpty(skuCode)) return null;
        Expression qualifier = ExpressionFactory.matchExp(TMtSku.SKU_CODE_PROPERTY, skuCode);
        SelectQuery select = new SelectQuery(TMtSku.class, qualifier);
        List list = context.performQuery(select);
        if (null == list || list.isEmpty()) return null;
        return (TMtSku) list.get(0);
    }

}
