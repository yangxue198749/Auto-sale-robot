package com.kdx.vms.webapi;

import com.kdx.vms.model.bean.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/16.
 */
class WebApiDao {
    private ObjectContext context;
    WebApiDao( ObjectContext context){
        this.context=context;
    }

    List<TOrderDetail> findSaleOrderList(){
//        try {
//        Expression qualifier = ExpressionFactory.matchExp(TSlOrderMaster.VENDOUT_PROPERTY+"."+TSlOrderVendout.VENDOUT_STATUS_PROPERTY, TSlOrderVendout.OUT_STATUS_SUCCESS);
//        Expression sysnMarkExpression = ExpressionFactory.noMatchExp(TSlOrderMaster.SYNC_MARK_PROPERTY,2).orExp(ExpressionFactory.matchExp(TSlOrderSale.SYNC_MARK_PROPERTY,null));
//        qualifier = qualifier.andExp(sysnMarkExpression);
//        qualifier = qualifier.andExp(ExpressionFactory.greaterExp(TSlOrderMaster.MT_VM_PROPERTY,0));
//        qualifier = qualifier.andExp(ExpressionFactory.greaterExp(TSlOrderMaster.SKU_PROPERTY,0));
//        SelectQuery select = new SelectQuery(TSlOrderMaster.class, qualifier);
//            select.addPrefetch(TSlOrderMaster.MT_VM_PROPERTY);
//            select.addPrefetch(TSlOrderMaster.SKU_PROPERTY);
//            select.addPrefetch(TSlOrderMaster.VENDOUT_PROPERTY+"."+TSlOrderVendout.CHANNEL_PROPERTY);
//            select.addPrefetch(TSlOrderMaster.MT_VM_PROPERTY+"."+ TMtVm.NODE_PROPERTY);
//            select.addPrefetch(TSlOrderMaster.COMPANY_PROPERTY);
//            select.addPrefetch(TSlOrderMaster.COMPANY_PROPERTY+"."+ TMtCompany.CUSTOMER_PROPERTY);
//            select.setPageSize(1000);
//        return context.performQuery(select);
//         }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;

    }

    List<TErpDataSync> findErpDataSync(){
//        try {
//            Expression qualifier = ExpressionFactory.matchExp(TErpDataSync.SYNC_MARK_PROPERTY,TErpDataSync.SYNC_FALSE);
//            SelectQuery select = new SelectQuery(TSlOrderSale.class, qualifier);
//            select.addOrdering(TErpDataSync.CTIME_PROPERTY,SortOrder.ASCENDING);
//            return context.performQuery(select);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

}
