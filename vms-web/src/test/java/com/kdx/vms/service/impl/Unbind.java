package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

/**
 * Created by yangshuxi on 2017/4/13.
 */
public class Unbind {

    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static String AREA_NO = "010";

    public static void main(String[] args) {
        unbind(67);
    }

    private static void unbind(String innerCode) {
        try {
            List list = null;
            SelectQuery query = new SelectQuery(TMtVm.class);
            if (innerCode.length() == 8 || innerCode.length() == 9) {
                query.setQualifier(ExpressionFactory.matchExp(TMtVm.INNER_CODE_PROPERTY, innerCode));
                list = context.performQuery(query);
            } else if (innerCode.length() >= 2 && innerCode.length() < 6) {
                int n = 5 - innerCode.length();
                StringBuilder areaNo = new StringBuilder(AREA_NO);
                for (int i = 0; i < n; i++) {
                    areaNo.append(0);
                }
                innerCode = areaNo.toString() + innerCode;
                query.setQualifier(ExpressionFactory.matchExp(TMtVm.INNER_CODE_PROPERTY, innerCode));
                list = context.performQuery(query);
            }
            if (null != list && list.size() == 1) {
                TMtVm mtVm = (TMtVm) list.get(0);
                mtVm.setUniqueCode(null);
                context.commitChanges();
                System.out.println("解绑" + innerCode + "完成");
            } else {
                System.out.println("解绑" + innerCode + "失败");
            }
        } catch (Exception e) {
            context.rollbackChanges();
            System.out.println("解绑" + innerCode + "失败");
        }
    }

    private static void unbind(String... innerCodes) {
        if (null != innerCodes) {
            for (String innerCode : innerCodes) {
                unbind(innerCode);
            }
        }
    }

    private static void unbind(Integer... innerCodes) {
        if (null != innerCodes) {
            for (Integer innerCode : innerCodes) {
                unbind(innerCode + "");
            }
        }
    }


}
