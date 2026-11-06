package com.kdx.mp.service.impl;

import com.kdx.mp.commons.util.WeChatMessageUtil;
import com.kdx.mp.util.WeChatUtils;
import com.kdx.mp.service.IMpCallbackService;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/28.
 */
@Service
public class MpCallbackServiceImpl implements IMpCallbackService{
    private final Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(getClass());
    @Override
    public String text(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String content = map.get("Content");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(content))return null;
        logger.info("魔核收到来自openId="+openId+"的文本消息:"+content);
        return null;
    }

    @Override
    public String image(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String picUrl = map.get("PicUrl");
        String mediaId = map.get("MediaId");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        logger.info("魔核收到来自openId="+openId+"的图片消息，链接="+picUrl+",mediaId="+mediaId);
        return null;
    }

    @Override
    public String voice(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String format = map.get("Format");
        String mediaId = map.get("MediaId");
        String recognition = map.get("Recognition");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        if(null!=recognition){
            logger.info("魔核收到来自openId="+openId+"的语音消息，格式="+format+",mediaId="+mediaId+",语音识别结果"+recognition);
        }
        else{
            logger.info("魔核收到来自openId="+openId+"的语音消息，格式="+format+",mediaId="+mediaId);
        }
        return null;
    }

    @Override
    public String video(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        logger.info("魔核收到来自openId="+openId+"的视频消息，thumbMediaId="+thumbMediaId+",mediaId="+mediaId);
        return null;
    }

    @Override
    public String shortvideo(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        logger.info("魔核收到来自openId="+openId+"的小视频消息，thumbMediaId="+thumbMediaId+",mediaId="+mediaId);
        return null;
    }

    @Override
    public String location(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String locationX = map.get("Location_X");
        String locationY = map.get("Location_Y");
        String scale = map.get("Scale");
        String label = map.get("Label");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        logger.info("魔核收到来自openId="+openId+"的地理位置消息，横坐标="+locationX+",纵坐标="+locationY+",地图缩放="+scale+",标题="+label);
        // 地理位置上报不支持回复用户
        return null;
    }

    @Override
    public String link(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String url = map.get("Url");
        String title = map.get("Title");
        String description = map.get("Description");
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        logger.info("魔核收到来自openId="+openId+"的链接消息，地址="+url+",标题="+title+",描述="+description);
        return null;
    }

    @Override
    public String event(Map<String, String> map) {
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String event = map.get("Event");
        logger.info("收到事件类型消息，具体类型为"+event);
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(event))return null;
        switch (event){
            case "subscribe":
                return subscribeEvent(map);
            case "unsubscribe":
                return unSubscribeEvent(map);
            case "SCAN":
                return scanEvent(map);
            case "LOCATION":
                return locationEvent(map);
            case "CLICK":
                return clickEvent(map);
            case "VIEW":
                return viewEvent(map);
            case "TEMPLATESENDJOBFINISH":
                return templateSendJobFinishEvent(map);
            default:
                return null;
        }
    }

    private String subscribeEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String eventKey = map.get("EventKey");
        if(!StringUtil.isEmpty(eventKey)&&eventKey.contains("qrscene_")){
            eventKey=eventKey.substring(8);
            String ticket = map.get("Ticket");
            String url = WeChatUtils.getQrImgUrl(ticket);
            logger.info("openId="+openId+"通过扫描二维码关注魔核公众号，二维码id为"+eventKey+",二维码ticket="+ticket+",二维码图片地址为"+url);
        }
        else{
            logger.info("openId="+openId+"关注魔核公众号");
        }
        return null;
    }
    private String unSubscribeEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        logger.info("openId="+openId+"取消关注魔核公众号");
        return null;
    }
    private String scanEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String ticket = map.get("Ticket");
        String eventKey = map.get("EventKey");
        String url = WeChatUtils.getQrImgUrl(ticket);
        logger.info("魔核收到来自openId="+openId+"的扫描带参数二维码事件，二维码id为"+eventKey+",二维码ticket="+ticket+",二维码图片地址为"+url);
        //扫描带参数二维码事件不支持回复
        return null;
    }
    private String locationEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String latitude = map.get("Latitude");
        String longitude = map.get("Longitude");
        String precision = map.get("Precision");
        logger.info("魔核收到来自openId="+openId+"的上报地理位置事件，维度="+latitude+",经度="+longitude+",精确度="+precision);
        return null;
    }
    private String clickEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String eventKey = map.get("EventKey");
        logger.info("魔核收到来自openId="+openId+"的点击拉取消息自定义菜单事件，eventKey="+eventKey);
        String content="";
        switch (eventKey){
            case "text1":content="魔核系统：\n" +
                    "「魔核」公众号是新众聚联公司，针对用户希望独立、远程、实时管理运营的需求，开发的移动管理工具。 \n" +
                    "该公众号基于「魔核」物联网运营管理系统及大数据技术，实现了用户实时查询、监控、管理设备运营情况，商品销售情况，综合数据报表等功能。全面支持用户智慧运营的核心需求。";
                break;
            case "text2":content="合作模式：\n" +
                    "新众聚联为客户提供了多种合作模式，具体请查询公司官网。\n" +
                    "www.touchfound.com";
                break;
            case "text3":content="联系我们：\n" +
                    "24小时服务电话：\n" +
                    "400-818-0009";
                break;
        }
        return WeChatMessageUtil.createTextMessage(openId,account,content);
    }
    private String viewEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String eventKey = map.get("EventKey");
        logger.info("魔核收到来自openId="+openId+"的点击跳转链接自定义菜单事件，eventKey="+eventKey);
        return null;
    }
    private String templateSendJobFinishEvent(Map<String, String> map){
        String openId = map.get("FromUserName");
        String account = map.get("ToUserName");
        String status = map.get("Status");
        switch (status){
            case "success":
                logger.info("魔核收到来自openId="+openId+"的发送模板消息完成事件，状态=成功");
                break;
            default:
                if(status.contains("block")){
                    logger.info("魔核收到来自openId="+openId+"的发送模板消息完成事件，状态=失败，用户拒绝接收");
                    break;
                }
                else{
                    logger.info("魔核收到来自openId="+openId+"的发送模板消息完成事件，状态=失败（非用户拒绝）");
                    break;
                }
        }
        //模板消息发送事件不支持回复
        return null;
    }
}
