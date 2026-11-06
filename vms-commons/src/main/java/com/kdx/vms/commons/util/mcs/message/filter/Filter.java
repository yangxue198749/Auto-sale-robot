package com.kdx.vms.commons.util.mcs.message.filter;

/**
 *
 * Created by liyang on 2016/11/9.
 */
public interface Filter {

    /**
     * 编码器
     * @param message
     * @return
     */
    public String encode(String message);

    /**
     * 解码器
     * @param message
     * @return
     */
    public String decode(String message);

}
