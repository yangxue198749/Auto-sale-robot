package com.kdx.vms.commons.util.mcs.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.kdx.vms.commons.util.mcs.message.filter.DefaultFilter;
import com.kdx.vms.commons.util.mcs.message.filter.Filter;

/**
 *
 * Created by liyang on 2016/11/7.
 */
public class MessageFactory {

    private Filter filter;

    public MessageFactory() {
        this.filter = new DefaultFilter();
    }

    public MessageFactory(Filter filter) {
        this.filter = null == filter ? new DefaultFilter() : filter;
    }

    public String toJson(HeadMessage headMessage) {
        if (null == headMessage) return null;
        return filter.encode(JSON.toJSONString(headMessage));
    }

    public HeadMessage toHeadMessageFormJson(String message) throws Exception {
        return JSON.parseObject(filter.decode(message), HeadMessage.class);
    }

    public HeadMessage createHeadMessage() {
        return new HeadMessage();
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        if (null != filter) this.filter = filter;
    }
    public MessageResult toMessageResult(String messageResultStr) throws JSONException{
    	 return JSON.parseObject(filter.decode(messageResultStr), MessageResult.class);
    }
}
