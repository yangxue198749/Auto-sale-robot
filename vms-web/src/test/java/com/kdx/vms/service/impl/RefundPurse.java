package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.mq.PublisherPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangshuxi on 2018/4/26.
 */
public class RefundPurse {
    public static final String topic = "topic.pay.refund.launch";
    private static Logger logger = LoggerFactory.getLogger(RefundPurse.class);

    public static void refund(String... seqs) {
        Arrays.stream(seqs).forEach(RefundPurse::refund);
        System.out.println("done...");
    }

    public static void refund(Long... seqs) {
        Arrays.stream(seqs).map(l -> l + "").forEach(RefundPurse::refund);
        System.out.println("done...");
    }

    public static void refund(String seq) {
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("refund seq=" + seq);
            String refundSeq = seq + "_r0";
            Map<String, Object> head = new HashMap<>();
            head.put("ts", System.currentTimeMillis());
            Map<String, Object> body = new HashMap<>();
            body.put("seq", refundSeq);
            Map<String, Object> msg = new HashMap<>();
            msg.put("head", head);
            msg.put("body", body);
            PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"), JSON.toJSONString(msg), topic);
            System.out.println(seq + " done...");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void main(String[] args) {
        refund("180522000003");
    }
}
