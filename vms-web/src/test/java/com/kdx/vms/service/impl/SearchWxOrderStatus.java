package com.kdx.vms.service.impl;

import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.commons.util.Md5Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yangshuxi on 2017/8/17.
 */
public class SearchWxOrderStatus {
    public static void main(String[] args) throws IOException {
//        System.out.println(searchOrderStatus("0516000221502928906993"));
        String nonce_str = Md5Util.md5(System.currentTimeMillis() + "touch-found");
        String appId = "wx5298b10e7e9638e0";
        String mch_id = "1436450402";
        String out_trade_no = "0516000221502928906993";
        System.out.println(sign(out_trade_no,nonce_str));
        Map<String,String> map = new HashMap<>();
        map.put("appid",appId);
        map.put("out_trade_no",out_trade_no);
        map.put("mch_id",mch_id);
        map.put("nonce_str",nonce_str);
        System.out.println(sign(map));
    }

    private static String searchOrderStatus(String outTradeNo) throws IOException {
        String addr = "https://api.mch.weixin.qq.com/pay/orderquery";
        String nonceStr = Md5Util.md5(System.currentTimeMillis() + "touch-found");
        String xml = "<xml>\n" +
                "   <appid>wx5298b10e7e9638e0</appid>\n" +
                "   <mch_id>1436450402</mch_id>\n" +
                "   <nonce_str>" + nonceStr + "</nonce_str>\n" +
                "   <out_trade_no>" + outTradeNo + "</out_trade_no>\n" +
                "   <sign>" + sign(outTradeNo, nonceStr) + "</sign>\n" +
                "</xml>";
        return WeChatUtils.post(addr, xml);
    }

    private static String sign(String outTradeNo, String nonceStr) {
        String url = "appid=wx5298b10e7e9638e0&mch_id=1436450402&nonce_str=" + nonceStr + "&out_trade_no=" + outTradeNo + "&key=bjxwjlxxkjtouchfoundNnetwork1201";
        return Md5Util.md5(url);
    }

    static String sign(Map<String, String> parameter) {
        String temp = Optional.ofNullable(parameter) // 初始化Optional
                .map(Map::keySet) // 获取keySet
                .map(Set::stream) // 生成stream
                .orElse(Stream.empty()) // 为null时
                .filter(Objects::nonNull) // 过滤非null
                .map(String::trim) // 去掉首尾空格
                .filter(s -> !"".equals(s)) // 过滤空串
                .filter(s -> null != parameter.get(s)) // 去掉空参数
                .filter(s -> !"".equals(parameter.get(s).trim())) // 去掉空串参数
                .filter(s -> !"sign".equalsIgnoreCase(s)) // 去掉sign
                .sorted(Comparator.naturalOrder()) // 排序
                .map(s -> s + "=" + parameter.get(s)) // 拼接单个key=value
                .collect(Collectors.joining("&")); // 拼接&
        System.out.println(temp + "&key=bjxwjlxxkjtouchfoundNnetwork1201");
        return Md5Util.md5(temp + "&key=bjxwjlxxkjtouchfoundNnetwork1201");
    }


}
