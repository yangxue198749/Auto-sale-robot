package com.kdx.vms.commons;

import java.util.*;

/**
 * Created by yangshuxi on 2017/3/24.
 */
public class DoubleMap<K1, K2, V> {
    private List<DoubleMapObject<K1, K2, V>> list = new ArrayList<>();

    public void clear(){
        list.clear();
    }

    public void put(K1 key1, K2 key2, V value) {
        if (null == key1 || null == key2) {
//            throw new NullPointerException();
            return;
        }
        DoubleMapObject<K1, K2, V> doubleMapObject = new DoubleMapObject<>(key1, key2, value);
        boolean flag = true;
        for (DoubleMapObject<K1, K2, V> object : list) {
            if (object.key1.equals(key1) && object.key2.equals(key2)) {
                object.value = value;
                flag = false;
                break;
            }
        }
        if (flag) list.add(doubleMapObject);
    }

    public V get(K1 key1, K2 key2) {
        for (DoubleMapObject<K1, K2, V> object : list) {
            if (object.key1.equals(key1) && object.key2.equals(key2)) {
                return object.value;
            }
        }
        return null;
    }

    public Map<K2, V> getMapByKey1(K1 key1) {
        Map<K2, V> map = new HashMap<>();
        for (DoubleMapObject<K1, K2, V> object : list) {
            if (object.key1.equals(key1)) {
                map.put(object.key2, object.value);
            }
        }
        return map;
    }

    public Map<K1, V> getMapByKey2(K2 key2) {
        Map<K1, V> map = new HashMap<>();
        for (DoubleMapObject<K1, K2, V> object : list) {
            if (object.key2.equals(key2)) {
                map.put(object.key1, object.value);
            }
        }
        return map;
    }

    public Map<K2, V> getMapByKey() {
        Map<K2, V> map = new HashMap<>();
        for (DoubleMapObject<K1, K2, V> object : list) {
            map.put(object.key2, object.value);
        }
        return map;
    }
    /*public Map<K1, V> getMapByKey() {
        Map<K1, V> map = new HashMap<>();
        for (DoubleMapObject<K1, K2, V> object : list) {
            map.put(object.key1, object.value);
        }
        return map;
    }*/

    public int size() {
        return list.size();
    }

    public Iterator<DoubleMapObject<K1, K2, V>> iterator() {
        return list.iterator();
    }
}
