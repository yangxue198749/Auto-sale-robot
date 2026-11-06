package com.kdx.vms.commons.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/11/9.
 */
public class CardCache {
    private static Logger logger = LoggerFactory.getLogger(CardCache.class);
    public static Map<Integer, String> PAY_TYPE_MAP = new HashMap<>();

    public static String get(Integer code) {
        return PAY_TYPE_MAP.get(code);
    }

    public static void refresh(Map<Integer, String> map) {
        try {
            PAY_TYPE_MAP = map;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}
