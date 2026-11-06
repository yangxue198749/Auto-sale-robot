package com.kdx.mp.updateMenu;

/**
 * Created by yangshuxi on 2017/5/26.
 */
public class CreateWhiteIp {

    public static final String IP1 = "1.180.206.";
    public static final String IP2 = "1.180.235.";

    public static void main(String[] args) {
        print(IP1, 192, 207);
        print(IP2, 32, 39);
    }

    public static void print(String ip, int begin, int end) {
        for (int i = begin; i <= end; i++) {
            System.out.println(ip + i);
        }
    }
}
