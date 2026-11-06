package com.kdx.vms.orm.dao.impl;

/**
 * Created by yangshuxi on 2016/12/2.
 */
public class TestBatchInsert {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            TestInsert insert = new TestInsert();
            new Thread(insert, "thread" + i).start();
        }
    }

}
