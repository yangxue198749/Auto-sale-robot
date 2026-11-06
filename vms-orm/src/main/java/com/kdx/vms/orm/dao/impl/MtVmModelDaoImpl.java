package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtVmModelDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liyang on 16-11-30.
 */
@Service("mtVmModelDao")
public class MtVmModelDaoImpl implements MtVmModelDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List findMtVmModelAll(ObjectContext context, Boolean showShortcuts) {
        logger.debug("context:" + context);
        if (null == context) return null;
        try {
            Expression expression = ExpressionFactory.expTrue();
            expression = expression.andExp(ExpressionFactory.matchExp(TMtVmModel.DELETE_MARK_PROPERTY, 0));//1:禁用;0:启用
            if(showShortcuts){
                expression = expression.andExp(ExpressionFactory.greaterExp(TMtVmModel.SHORTCUTS_QTY_PROPERTY, 0));//不展示闪购键数量为0的机型
            }
            SelectQuery select = new SelectQuery(TMtVmModel.class, expression);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TMtVmModel findMtVmModelById(ObjectContext context, Integer vmModelId) {
        try {
            ObjectId id = new ObjectId(TMtVmModel.class.getSimpleName(), TMtVmModel.MODEL_ID_PK_COLUMN, vmModelId);
            ObjectIdQuery query = new ObjectIdQuery(id);
            return (TMtVmModel) Cayenne.objectForQuery(context, query);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> findVmModel(String name, Integer pageSize, ObjectContext context) {
        SelectQuery query = new SelectQuery(TMtVmModel.class);
        if (StringUtils.isNotEmpty(name)) {
            query.setQualifier(ExpressionFactory.likeExp(TMtVmModel.MODEL_NAME_PROPERTY, "%" + name + "%"));
        }
        return context.performQuery(query);
    }

    @Override
    public List<?> findMtVmModelByCombinatorialPatterns(TSysUser user, Object... combinatorialPatterns) {
        if (null == user) return null;
        logger.info("search vmModel user=" + user.getUserAccount());
        try {
            Expression qualifier = ExpressionUtils.fillMatchQualifier(null, TMtVmModel.DELETE_MARK_PROPERTY, 0);//1:禁用;0:启用
            qualifier = ExpressionUtils.fillInQualifier(qualifier, TMtVmModel.COMBINATORIAL_PATTERN_PROPERTY, combinatorialPatterns);
            SelectQuery select = new SelectQuery(TMtVmModel.class, qualifier);
            return user.getObjectContext().performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
