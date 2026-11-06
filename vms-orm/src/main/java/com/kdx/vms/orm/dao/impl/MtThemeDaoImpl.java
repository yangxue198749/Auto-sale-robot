package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtTheme;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtThemeDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 *
 * Created by liyang on 2017/5/3.
 */
@Service("mtThemeDao")
public class MtThemeDaoImpl implements MtThemeDao {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List findThemesByName(ObjectContext context, String themeName, Integer pageSize) {
        LOGGER.info("context:" + context + ", themeName:" + themeName + ", pageSize:" + pageSize);
        try {
            if (null == context) {
                throw new NullPointerException("context is null.");
            }
            Expression expression = ExpressionFactory.matchExp(TMtTheme.DELETE_MARK_PROPERTY, DeleteMark.EXIST);
            if (!StringUtil.isEmpty(themeName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TMtTheme.THEME_NAME_PROPERTY, "%" + themeName + "%"));
            }
            SelectQuery selectQuery = new SelectQuery(TMtTheme.class, expression);
            selectQuery.setPageSize(IntegerUtils.isPositiveInteger(pageSize) ? pageSize : 10);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

}
