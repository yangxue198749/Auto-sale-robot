package com.kdx.vms.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangguodong on 2017/8/30.
 */
@Controller
public class HeartController {
    static Map<String,Object> map = null;

    static {
        map = new HashMap<String,Object>();
        map.put("heart","I'm OK");

    }
    @RequestMapping("heart")
    @ResponseBody
    public Map<String,Object> heart(){
        return map;
    }
}
