package com.kdx.vms.mq.consumer;

/**
 * Created by yangshuxi on 2017/7/17.
 */
public class HandleResponse {
    private String respTopic;
    private String respExchange;
    private String result;

    public String getRespTopic() {
        return respTopic;
    }

    public void setRespTopic(String respTopic) {
        this.respTopic = respTopic;
    }

    public String getRespExchange() {
        return respExchange;
    }

    public void setRespExchange(String respExchange) {
        this.respExchange = respExchange;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
