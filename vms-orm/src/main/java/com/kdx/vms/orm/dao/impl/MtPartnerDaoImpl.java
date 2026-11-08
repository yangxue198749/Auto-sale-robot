package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtPartnerDao;

@Repository
public class MtPartnerDaoImpl implements MtPartnerDao {

	@Override
	public List<Object> findPartnerList(String partnerName, String manageName, String partnerType, Integer pageSize,
			Integer pageNo ,TSysUser user ,ObjectContext context) {
		SelectQuery query =new SelectQuery(TMtPartner.class,ExpressionFactory.expTrue());
		
		TMtCompany company = user.getCompany();
		switch (company.getCompanyLevel()) {
		case TMtCompany.LEVEL_ONE:
			break;
		case TMtCompany.LEVEL_TWO:
            Expression expression = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY,company.getChildCompany());
            expression = expression.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY,company));
			query.andQualifier(expression);
			break;
		case TMtCompany.LEVEL_THREE:
			query.andQualifier(ExpressionFactory.matchExp(TMtPartner.COMPANY_PROPERTY,company));
			break;
		default:
			break;
		}
		
		if(StringUtils.isNoneEmpty(partnerName)){
			query.andQualifier(ExpressionFactory.likeExp(TMtPartner.PARTNER_NAME_PROPERTY, "%"+partnerName+"%"));
		}
		if(StringUtils.isNoneEmpty(manageName)){
			query.andQualifier(ExpressionFactory.likeExp(TMtPartner.MANAGER_NAME_PROPERTY, "%"+manageName+"%"));
		}
		if(StringUtils.isNoneEmpty(partnerType)){
			query.andQualifier(ExpressionFactory.matchExp(TMtPartner.PARTNER_TYPE_PROPERTY, partnerType));
		}
		query.addOrdering(TMtPartner.CTIME_PROPERTY,SortOrder.DESCENDING);
		query.setPageSize(pageSize);
		return context.performQuery(query);
	}

}
