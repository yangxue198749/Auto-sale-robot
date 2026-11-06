package com.kdx.vms.commons.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/2.
 */
public class ReturnCodeUtil {
    public final static String KEY_CODE = "code";
    public final static String KEY_MESSAGE = "message";
    public final static String KEY_TYPE = "type";
    public static Map<String,Object> fillResultMap( Map<String,Object> respMap){
        Integer code= (Integer) respMap.get(KEY_CODE);
        respMap.put(KEY_MESSAGE,message_map.get(code));
        respMap.put(KEY_TYPE,type_map.get(code));
        return respMap;
    }
    public static Map<Integer,String> message_map= new HashMap<>();
    static{
        message_map.put(0,"成功");
        message_map.put(101,"系统内部错误，失败");
        message_map.put(102,"提交数据不完整，失败");
        message_map.put(103,"百分比打折不可大于100，失败");
//        message_map.put(104,"不可低于原价，失败");
        message_map.put(105,"调整量不可大于100或小于1，失败");
    }

    public static Map<Integer,String> type_map= new HashMap<>();
    static{
        type_map.put(0,"success");
        type_map.put(101,"error");
        type_map.put(102,"error");
        type_map.put(103,"error");
//        type_map.put(104,"error");
    }
}
