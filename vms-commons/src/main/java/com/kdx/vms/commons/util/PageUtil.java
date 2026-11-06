package com.kdx.vms.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PageUtil {
    static Logger logger = LoggerFactory.getLogger(PageUtil.class);

    /**
     * 获取子集
     *
     * @param pageNo     当前页码  最小为1
     * @param pageSize
     * @param parentList
     * @return
     */
    public static <E> List<E> formatList(Integer pageNo, Integer pageSize, List<E> parentList) {
        try {
            return Optional.ofNullable(parentList).orElse(Collections.emptyList()).stream().skip((pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    public static <K, V> List<V> mapToList(Map<K, V> map) {
        List<V> list = new ArrayList<>();
        if (null != map && map.size() > 0) {
            for (K key : map.keySet()) {
                list.add(map.get(key));
            }
        }
        return list;
    }
}
