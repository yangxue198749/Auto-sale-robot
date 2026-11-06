package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.ReserveStatisticsDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by yangshuxi on 2017/8/3.
 */
@Repository
public class ReserveStatisticsDaoImpl implements ReserveStatisticsDao {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<?> findTodayReserveStatisticsByVm(ObjectContext context, TMtVm mtVm) {
        try {
            String date = LocalDate.now().toString();
            int reserveEndTime = Integer.parseInt((String) PropertiesLoader.map.get("reserveEndTime"));
            if (LocalDateTime.now().getHour() >= reserveEndTime) {
                date = LocalDate.now().plusDays(1).toString();
            }
            if (mtVm.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) mtVm = mtVm.getParentVm();
            Expression qualifier = ExpressionFactory.matchExp(TReserveStatistics.VM_PROPERTY, mtVm);
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TReserveStatistics.STATISTICS_DATE_PROPERTY, date));
            SelectQuery select = new SelectQuery(TReserveStatistics.class, qualifier);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findPage(ObjectContext context, String innerCode, String skuName, String statisticsDate, Integer pageSize, TSysUser user) {
        try {

            Expression qualifier = ExpressionFactory.expTrue();
            TMtCompany company = user.getCompany();
            int level = company.getCompanyLevel();
            switch (level) {
                case TMtCompany.LEVEL_ONE:
                    break;
                case TMtCompany.LEVEL_TWO:
                    Expression expression = ExpressionFactory.inExp(TReserveStatistics.VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    expression = expression.orExp(ExpressionFactory.matchExp(TReserveStatistics.VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(expression);
                    break;
                case TMtCompany.LEVEL_THREE:
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TReserveStatistics.VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company));
                    break;

                default:
                    break;
            }
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TReserveStatistics.VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, innerCode);
            qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TReserveStatistics.TOTALNUM_PROPERTY, 0));
            if (!StringUtil.isEmpty(statisticsDate)) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TReserveStatistics.STATISTICS_DATE_PROPERTY, statisticsDate));
            }
            if (!StringUtil.isEmpty(skuName) && !StringUtil.isEmpty(skuName.trim())) {
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TReserveStatistics.SKU_PROPERTY + "." + TMtSku.SKU_NAME_PROPERTY, "%" + skuName.trim() + "%"));
            }
            SelectQuery select = new SelectQuery(TReserveStatistics.class, qualifier);
            select.addOrdering(TReserveStatistics.STATISTICS_DATE_PROPERTY, SortOrder.DESCENDING);
            select.addPrefetch(TReserveStatistics.SKU_PROPERTY);
            select.addPrefetch(TReserveStatistics.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
            select.addPrefetch(TReserveStatistics.VM_PROPERTY);
            if (null != pageSize) {
                select.setPageSize(pageSize);
            }
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
