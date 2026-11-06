package com.kdx.vms.web.tags;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.orm.cache.AreaCache;

/**
 *
 * Created by yangshuxi on 2016/12/5.
 */
public class AreaUtils {

    public static TMtArea getArea(Integer id) {
        return AreaCache.getArea(id);
    }

    public static String getAreaName(Integer id) {
        return AreaCache.getAreaName(id);
    }

    public static String getAreaJson(){
        return AreaCache.getAreaJson();

    }
    public static String getAreaNameById(Integer id){
        TMtArea area =  AreaCache.getArea(id);
        if(null != area ){
            if(area.getAreaLevel() == 2){
                //省
                return area.getAreaName();
            }else if(area.getAreaLevel()==3){
                //市
                return area.getParentArea().getAreaName() +area.getAreaName();
            }else if(area.getAreaLevel() == 4){
                //区
                return area.getParentArea().getParentArea().getAreaName() +area.getParentArea().getAreaName()+area.getAreaName();
            }
        }
        return "";
    }


}