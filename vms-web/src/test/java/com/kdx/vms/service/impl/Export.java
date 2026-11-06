package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.WebUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yangshuxi on 2017/10/10.
 */
public class Export {

    public static void main(String[] args) throws Exception {
        int first = 3;
        int last = 2;
//        exportALL(first,last);
        exportByDay(first, last);
    }

    public static void exportALL(Integer begin, Integer end) {
        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(String.valueOf(20)));
        executor.execute(new SlExp(DateUtils.getDayBeforeMin(begin), DateUtils.getDayBeforeMax(end)));
        executor.shutdown();
    }

    public static void exportByDay(Integer first, Integer last) {
        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(String.valueOf(20)));
        for (int i = first; i > last; i--) {
            Date begin = DateUtils.getDayBeforeMin(i);
            Date end = DateUtils.getDayBeforeMax(i);
            executor.execute(new SlExp(begin, end));
        }
        executor.shutdown();
    }


    public void add() throws Exception {
        String loginUrl = "http://vms.touchfound.net/submitLogin";
        Map<String, String> map = new HashMap<>();
        map.put("validCode", "1111");
        map.put("userName", "vmssysadmin");
        map.put("password", "1111");
        String re = WebUtils.doGet(loginUrl, map);
        System.out.println(re);
        String addUrl = "http://vms.touchfound.net/mtVm/addMtVm";
        Map<String, String> map1 = new HashMap<>();
        map1.put("vmDesc", "测试远程调用接口");
        map1.put("printer", "0");
        map1.put("reserve", "0");
        map1.put("vmModelId", "7802");
        map1.put("nodeId", "5443");
        String result = WebUtils.doPost(addUrl, map1);
        System.out.println(result);
    }
}
