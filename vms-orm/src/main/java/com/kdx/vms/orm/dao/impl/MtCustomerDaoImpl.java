package com.kdx.vms.orm.dao.impl;

import java.util.Collections;
import java.util.List;

import com.kdx.vms.commons.util.StringUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.orm.dao.MtCustomerDao;

@Repository
public class MtCustomerDaoImpl implements MtCustomerDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findCustomerList(String customerName, String manageName, String customerMode,
			String customerType, Integer pageSize, ObjectContext context) {
		return this.findCustomerList(null, customerName, manageName, customerMode, customerType, pageSize, context);
	}

	@Override
	public List<Object> findCustomerList(List<Integer> idList, String customerName, String manageName,
			String customerMode, String customerType, Integer pageSize, ObjectContext context) {
		SelectQuery query =new SelectQuery(TMtCustomer.class,ExpressionFactory.expTrue());
		if(StringUtils.isNoneEmpty(customerName)){
			query.andQualifier(ExpressionFactory.likeExp(TMtCustomer.CUSTOMER_NAME_PROPERTY, "%"+customerName));
		}
		if(StringUtils.isNoneEmpty(manageName)){
			query.andQualifier(ExpressionFactory.likeExp(TMtCustomer.MANAGER_NAME_PROPERTY, "%"+manageName));
		}
		if(StringUtils.isNoneEmpty(customerMode)){
			query.andQualifier(ExpressionFactory.matchExp(TMtCustomer.CUSTOMER_MODE_PROPERTY, customerMode));
		}
		if(StringUtils.isNoneEmpty(customerType)){
			query.andQualifier(ExpressionFactory.matchExp(TMtCustomer.CUSTOMER_TYPE_PROPERTY, customerType));
		}
		if(idList != null && idList.size() > 0){
			query.andQualifier(ExpressionFactory.inExp(TMtCustomer.CTOR_PROPERTY, idList));
		}
		query.addOrdering(TMtCustomer.CTIME_PROPERTY,SortOrder.DESCENDING);
		query.setPageSize(pageSize);
		return context.performQuery(query);
	}

	@Override
	public List<TMtCustomer> findUnusedCustomer(ObjectContext context){
//		String sql = "select * from T_MT_CUSTOMER where CUSTOMER_ID not in (#bind($ids))";
		String sql = "select * from T_MT_CUSTOMER where CUSTOMER_ID not in ( select CUSTOMER_ID from T_MT_COMPANY)";
		SQLTemplate query =new SQLTemplate(TMtCustomer.class , sql);
//		query.setParameters(Collections.singletonMap("ids", list));
		return context.performQuery(query);
	}

    public List findCustomerB(ObjectContext context) {
        logger.info("context:" + context);
        if (null == context) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TMtCustomer.CUSTOMER_TYPE_PROPERTY, TMtCustomer.CUSTOMER_TYPE_B);
            SelectQuery selectQuery = new SelectQuery(TMtCustomer.class, expression);
            selectQuery.addPrefetch(TMtCustomer.COMPANY_PROPERTY);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public TMtCustomer findCustomerByPhone(ObjectContext context, String phone) {
        logger.info("context:" + context + ", phone:" + phone);
        if (null == context || StringUtil.isEmpty(phone)) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TMtCustomer.MANAGER_PHONE_PROPERTY, phone);
            SelectQuery selectQuery = new SelectQuery(TMtCustomer.class, expression);
            return (TMtCustomer) Cayenne.objectForQuery(context, selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}