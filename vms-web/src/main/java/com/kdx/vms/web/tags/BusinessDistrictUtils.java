package com.kdx.vms.web.tags;

import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/7.
 */
public class BusinessDistrictUtils {
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    public static TMtBusinessDistrict getBusinessDistrict(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtBusinessDistrict.class, id);
    }

    public static String getBusinessDistrictName(Integer id) {
        if (null == id || id <= 0) return null;
        if(Cayenne.objectForPK(context, TMtBusinessDistrict.class, id)==null) return null;
        return Cayenne.objectForPK(context, TMtBusinessDistrict.class, id).getBusinessDistrictName();
    }

    public static List<TMtBusinessDistrict> getBusinessDistrictList(){
        Expression qualifier = ExpressionFactory.expTrue();
        SelectQuery select = new SelectQuery(TMtBusinessDistrict.class, qualifier);
        return context.performQuery(select);
    }
}
