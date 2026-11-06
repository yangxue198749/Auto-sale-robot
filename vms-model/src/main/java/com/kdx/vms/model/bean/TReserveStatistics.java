package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.excel.ExcelField;
import com.kdx.vms.model.bean.auto._TReserveStatistics;
import com.kdx.vms.model.enumeration.OrderVendoutStatus;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class TReserveStatistics extends _TReserveStatistics {

    @ExcelField(title = "日期", align = 2, sort = 1)
    public String getDate() {
        return getStatisticsDate();
    }

    @ExcelField(title = "售货机编号", align = 2, sort = 2)
    public String getInnerCode() {
        return getVm().getInnerCode();
    }

    @ExcelField(title = "商品名称", align = 2, sort = 3)
    public String getSkuName() {
        return StringUtil.isEmpty(getSku().getSkuUnit()) ? getSku().getSkuName() : getSku().getSkuName() + getSku().getSkuUnit();
    }

    @ExcelField(title = "待配送订单", align = 2, sort = 4)
    public Integer getPending() {
        return getPendingNum();
    }

    @ExcelField(title = "已配送订单", align = 2, sort = 5)
    public Integer getDistribution() {
        return getDistributionNum();
    }

    @ExcelField(title = "已取货订单", align = 2, sort = 6)
    public Integer getOutNum() {
        return findOutSuccessReserveCount(getVm().getId(), getSku().getId(), getStatisticsDate());
    }

    public Integer findOutSuccessReserveCount(Integer vmId, Integer skuId, String validDay) {
        try {
            Expression expression = ExpressionFactory.expTrue();
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY, vmId));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.VALID_TIME_PROPERTY, validDay));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.SKU_PROPERTY, skuId));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.RESERVE));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.SUCCESS));

            SelectQuery query = new SelectQuery(TOrderDetail.class, expression);
            List list = getObjectContext().performQuery(query);
            if (null != list) {
                return list.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
