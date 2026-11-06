package com.kdx.vms.orm.cache;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.bean.TSysElement;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangshuxi on 2017/5/3.
 */
public class ElementCache {
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static Set<String> elementSet = new HashSet<>();

    static {
        SelectQuery select = new SelectQuery(TSysElement.class);
        List list = context.performQuery(select);
        for(Object o:list){
            elementSet.add(((TSysElement)o).getElementUrl());
        }
    }

    public static Set<String> getElementSet() {
        return elementSet;
    }
}
