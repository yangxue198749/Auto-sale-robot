package com.kdx.mp.service;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/28.
 */
public interface IMpCallbackService {
    public String text(Map<String,String> map);
    public String image(Map<String,String> map);
    public String voice(Map<String,String> map);
    public String video(Map<String,String> map);
    public String shortvideo(Map<String,String> map);
    public String location(Map<String,String> map);
    public String link(Map<String,String> map);
    public String event(Map<String,String> map);
}
