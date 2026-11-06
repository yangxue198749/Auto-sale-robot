package com.kdx.vms.commons;

/**
 * Created by yangshuxi on 2017/3/24.
 */
public class DoubleMapObject<K1, K2, V> {
    K1 key1;
    K2 key2;
    V value;

    public DoubleMapObject(K1 key1, K2 key2, V value) {
        this.key1 = key1;
        this.key2 = key2;
        this.value = value;
    }
}
