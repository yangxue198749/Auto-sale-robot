package com.kdx.vms.web.oauth;

import com.kdx.vms.model.bean.TSysUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by zhangguodong on 2017/6/9.
 */
public class UserMap {

    private static Map<Serializable,TSysUser> map = new HashMap<>();

    public static TSysUser getUser(Serializable sessionId){
        return map.get(sessionId);
    }
    public static void putUser(Serializable sessionId,TSysUser user){
        map.put(sessionId,user);
    }
    public static void removeUser(Serializable sessionId){
        map.remove(sessionId);
    }
}
