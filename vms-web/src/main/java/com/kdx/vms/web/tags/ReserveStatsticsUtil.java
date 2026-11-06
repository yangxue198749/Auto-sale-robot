package com.kdx.vms.web.tags;

import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderMaster;
import com.kdx.vms.model.enumeration.OrderVendoutStatus;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

import static com.kdx.vms.webapi.WebApiTask.context;

/**
 * Created by zhangguodong on 2017/8/15.
 */
public class ReserveStatsticsUtil {

    /**
     * 获取取货成功的预售订单数量
     * @param vmId
     * @param skuId
     * @param validDay
     * @return
     */
    public static Integer findOutSuccessReserveCount(Integer vmId,Integer skuId,String validDay){
        try {
            Expression expression = ExpressionFactory.expTrue();
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY, vmId));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.VALID_TIME_PROPERTY, validDay));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.SKU_PROPERTY, skuId));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.RESERVE));
            expression = expression.andExp(ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.SUCCESS));

            SelectQuery query = new SelectQuery(TOrderDetail.class, expression);
            List list = context.performQuery(query);
            if(null != list ){
                return list.size();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
