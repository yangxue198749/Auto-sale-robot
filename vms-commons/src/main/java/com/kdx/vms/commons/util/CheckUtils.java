package com.kdx.vms.commons.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/11/29.
 */
public class CheckUtils {
    public static boolean check(String s) {
        return null != s && !s.trim().equals("");
    }

    public static boolean check(Integer i) {
        return null != i && i != 0;
    }

    public static boolean check(Long l) {
        return null != l && l != 0;
    }

    public static boolean check(Collection c) {
        return null != c && !c.isEmpty();
    }

    public static boolean check(Map m) {
        return null != m && m.isEmpty();
    }

    public static boolean check(Object o) {
        return null != o;
    }

    public static boolean check(Object... os) {
        return null != os && Arrays.stream(os).allMatch(CheckUtils::check);
    }
    public static boolean check(Collection... cs) {
        return null != cs && Arrays.stream(cs).allMatch(CheckUtils::check);
    }

}
