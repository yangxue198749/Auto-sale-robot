package com.kdx.vms.webapi;

/**
 * Created by yangshuxi on 2017/1/13.
 */
public class Counter{
    private int total;
    private int success = 0;
    private int element;

    Counter(int total, int element) {
        this.total = total;
        this.element = element;
    }

    public synchronized void plus(int s){
        success+=s;
    }

    public int getTotal() {
        return total;
    }

    public int getSuccess() {
        return success;
    }

    int getNum(){
        if(element<=0)element=1;
        Integer threadNum = total/element;
        if (total%element>0)threadNum++;
        return threadNum;
    }
}
