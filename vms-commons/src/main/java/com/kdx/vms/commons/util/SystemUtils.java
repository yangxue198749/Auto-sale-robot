package com.kdx.vms.commons.util;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * Created by liyang on 2017/4/25.
 */
public class SystemUtils {

//    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils.class);

    public static synchronized long createSn() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        return System.currentTimeMillis();
    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
