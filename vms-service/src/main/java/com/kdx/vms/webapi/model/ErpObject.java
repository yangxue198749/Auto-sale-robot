package com.kdx.vms.webapi.model;

import java.math.BigDecimal;

/**
 * Created by yangshuxi on 2016/12/16.
 */
public abstract class ErpObject {
    static String LIN="626b414ada7d218397cc438ee2e7c28f";
//    public String getJson(){
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        return gson.toJson(this);
//    }

     String convert(Integer money){
        BigDecimal bigDecimal=new BigDecimal(money);
        return bigDecimal.divide(new BigDecimal(100),2,BigDecimal.ROUND_FLOOR).toString();
    }
}
