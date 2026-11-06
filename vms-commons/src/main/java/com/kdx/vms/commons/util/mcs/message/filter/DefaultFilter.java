package com.kdx.vms.commons.util.mcs.message.filter;

/**
 *
 * Created by liyang on 2016/11/9.
 */
public class DefaultFilter implements Filter {

    @Override
    public String encode(String message) {
        return message;
    }

    @Override
    public String decode(String message) {
        return message;
    }

}
