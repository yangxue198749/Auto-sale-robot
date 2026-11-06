package com.kdx.vms.orm.cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;
import java.util.Optional;

/**
 * Created by yangshuxi on 2016/12/16.
 */
public class AreaCache {

    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static List areaList = null;

    static {
        SelectQuery select = new SelectQuery(TMtArea.class);
        select.addPrefetch(TMtArea.WARE_HOUSE_AREA_PROPERTY);
        areaList = context.performQuery(select);
    }

    //    private static List areaList = context.performQuery(new SelectQuery(TMtArea.class));
    public static TMtArea getArea(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtArea.class, id);
    }

    public static String getAreaName(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtArea.class, id).getAreaName();
    }

    public static String getAreaJson() {
        JSONArray array = new JSONArray();
        for (Object obj : areaList) {
            TMtArea mtArea = (TMtArea) obj;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", mtArea.getId());
            jsonObject.put("name", mtArea.getAreaName());
            jsonObject.put("pid", Optional.of(mtArea).map(TMtArea::getParentArea).map(TMtArea::getId).orElse(1));
            array.add(jsonObject);
        }
        return array.toString();
    }
}
