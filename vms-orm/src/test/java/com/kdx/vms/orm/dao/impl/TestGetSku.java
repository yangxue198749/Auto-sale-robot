package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmEventMaster;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangshuxi on 2016/12/5.
 */
public class TestGetSku {
    public static void main(String[] args) {
        ObjectContext context= ObjectContextFactory.getInstance().newObjectContext();
        TMtVmEventMaster mtVmEventMaster = context.newObject(TMtVmEventMaster.class);
        context.commitChanges();
        System.out.println("==============================================================");
        context.rollbackChanges();
    }

    public static  List<TMtVm> findListByCondition(ObjectContext context) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.EVENT_PROPERTY+"."+TMtVmEventMaster.EVENT_TYPE_PROPERTY,1);
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            List list = context.performQuery(select);
            List<Integer> l=new ArrayList<>();
            for (Object o:list){
                l.add(Cayenne.intPKForObject((TMtVm)o));
            }
            System.out.println(l);
//            Expression qualifier = ExpressionFactory.matchExp(TMtVmEventMaster.EVENT_TYPE_PROPERTY,1);
//            SelectQuery select = new SelectQuery(TMtVmEventMaster.class, qualifier);
//            List list = context.performQuery(select);
            System.out.println("===============================");
            Expression qualifier1 = ExpressionFactory.notInExp(TMtVm.VM_ID_PK_COLUMN,l);
//            qualifier1 = qualifier1.andExp(ExpressionFactory.matchExp(TMtVm.EVENT_PROPERTY + "." + TMtVmEventMaster.EVENT_TYPE_PROPERTY,1));
            SelectQuery select1 = new SelectQuery(TMtVm.class, qualifier1);
            List list1 = context.performQuery(select1);
            return list1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
