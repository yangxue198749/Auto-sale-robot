package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtSimDao;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by liyang on 16-11-30.
 */
@Service("mtSimDao")
public class MtSimDaoImpl implements MtSimDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List findMtSimPage(ObjectContext context, Integer pageSize) {
        logger.debug("context:" + context + ", pageSize:" + pageSize);
        if (null == context) return null;
        pageSize = null == pageSize || pageSize <= 1 ? 10 : pageSize;
        try {
            SelectQuery select = new SelectQuery(TMtSim.class);
            select.setPageSize(pageSize);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TMtSim findMtSimPk(ObjectContext context, Integer pk) {
        logger.debug("context:" + context + ", pk:" + pk);
        try {
            return Cayenne.objectForPK(context, TMtSim.class, pk);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findMtSim(ObjectContext context, String simPhone, String simNumber) {
        logger.debug("context:" + context + ", simPhone:" + simPhone + ", simNumber" + simNumber);
        if (null == context)
            return null;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtSim.SIM_PHONE_PROPERTY, simPhone);
            qualifier = qualifier.orExp(ExpressionFactory.matchExp(TMtSim.SIM_NUMBER_PROPERTY, simNumber));
            SelectQuery select = new SelectQuery(TMtSim.class, qualifier);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public List findMtSim(ObjectContext context ,TSysUser sysUser ,String simPhone, String simNumber, Integer serviceProvider, Boolean use, Integer pageSize) {
        logger.debug("context:" + context + ", simPhone:" + simPhone + ", simNumber:" + simNumber + ", serviceProvider:" + serviceProvider + ", pageSize:" + pageSize);
        if (null == context)
            return null;
        pageSize = null == pageSize || pageSize <= 1 ? 10 : pageSize;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtSim.DELETE_MARK_PROPERTY, 0);
            
            if(sysUser != null){
            	TMtCompany company = sysUser.getCompany();
            	switch (company.getCompanyLevel()) {
            	case TMtCompany.LEVEL_ONE:
            		break;
            	case TMtCompany.LEVEL_TWO:
            		List<TSysUser> userList = company.getUser();
            		List<TMtCompany> childCompany = company.getChildCompany();
            		for (TMtCompany tMtCompany : childCompany) {
            			userList.addAll(tMtCompany.getUser());
            		}
            		List<Integer> userIdList = new ArrayList<Integer>();
            		for (TSysUser user : userList) {
            			userIdList.add(user.getId());
					}
            		Expression expression = ExpressionFactory.inExp(TMtSim.CTOR_PROPERTY,userIdList);
            		expression = expression.orExp(ExpressionFactory.matchExp(TMtSim.CTOR_PROPERTY,sysUser.getId()));
            		qualifier = qualifier.andExp(expression);
            		break;
            	case TMtCompany.LEVEL_THREE:
            		qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSim.CTOR_PROPERTY,sysUser.getId()));
            		break;
            	default:
            		break;
            	}
            }
            
            // sim卡号 和 sim卡编号只能有其中之一
            if (null != simPhone) {
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtSim.SIM_PHONE_PROPERTY, "%" + simPhone + "%"));
            } else if (null != simNumber) {
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtSim.SIM_NUMBER_PROPERTY, "%" + simNumber + "%"));
            }
            // 使用中
            if (null != use) {
                qualifier = use ? qualifier.andExp(ExpressionFactory.noMatchExp(TMtSim.MT_VM_PROPERTY, null)) : qualifier.andExp(ExpressionFactory.matchExp(TMtSim.MT_VM_PROPERTY, null));
            }
            // 运营商
            if (null != serviceProvider) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSim.SERVICE_PROVIDER_PROPERTY, serviceProvider));
            }
            SelectQuery select = new SelectQuery(TMtSim.class, qualifier);
            select.addPrefetch(TMtSim.MT_VM_PROPERTY);
            select.setPageSize(pageSize);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}