package com.kdx.mp.commons.util;

import com.kdx.vms.commons.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/1/4.
 */
public class WeChatMessageUtil {
    public static String createTextMessage(String openId,String account,String content){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(content))return null;
        return "<xml>" +
                "<ToUserName><![CDATA["+openId+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+account+"]]></FromUserName>" +
                "<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA["+content+"]]></Content>" +
                "</xml>";
    }

    public static String createImageMessage(String openId,String account,String mediaId){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(mediaId))return null;
        return "<xml>" +
                "<ToUserName><![CDATA["+openId+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+account+"]]></FromUserName>" +
                "<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<Image>" +
                "<MediaId><![CDATA["+mediaId+"]]></MediaId>" +
                "</Image>" +
                "</xml>";
    }

    public static String createVoiceMessage(String openId,String account,String mediaId){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(mediaId))return null;
        return "<xml>" +
                "<ToUserName><![CDATA["+openId+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+account+"]]></FromUserName>" +
                "<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<Voice>" +
                "<MediaId><![CDATA["+mediaId+"]]></MediaId>" +
                "</Voice>" +
                "</xml>";
    }

    public static String createVideoMessage(String openId,String account,String mediaId,String title,String description){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||StringUtil.isEmpty(mediaId))return null;
        return "<xml>" +
                "<ToUserName><![CDATA["+openId+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+account+"]]></FromUserName>" +
                "<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Voice>" +
                "<MediaId><![CDATA["+mediaId+"]]></MediaId>" +
                "<Title><![CDATA["+title+"]]></Title>" +
                "<Description><![CDATA["+description+"]]></Description>" +
                "</Voice>" +
                "</xml>";
    }

    public static String createMusicMessage(String openId,String account,String title,String description,String musicUrl,String hqMusicUrl,String thumbMediaId){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account))return null;
        return "<xml>" +
                "<ToUserName><![CDATA["+openId+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+account+"]]></FromUserName>" +
                "<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Music>" +
                "<Title><![CDATA["+title+"]]></Title>" +
                "<Description><![CDATA["+description+"]]></Description>" +
                "<MusicUrl><![CDATA["+musicUrl+"]]></MusicUrl>" +
                "<HQMusicUrl><![CDATA["+hqMusicUrl+"]]></HQMusicUrl>" +
                "<ThumbMediaId><![CDATA["+thumbMediaId+"]]></ThumbMediaId>" +
                "</Music>" +
                "</xml>";
    }

    public static String createNews(String openId,String account,Map<String,String>... articles){
        if(StringUtil.isEmpty(openId)||StringUtil.isEmpty(account)||null==articles)return null;
        if(articles.length>8)return null;
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>" + "<ToUserName><![CDATA[").append(openId).append("]]></ToUserName>").append("<FromUserName><![CDATA[").append(account).append("]]></FromUserName>").append("<CreateTime>").append(System.currentTimeMillis() / 1000).append("</CreateTime>").append("<MsgType><![CDATA[news]]></MsgType>");
        xml.append("<ArticleCount>").append(articles.length).append("</ArticleCount>");
        xml.append("<Articles>");
        for(Map<String,String> map:articles){
            xml.append("<item>" + "<Title><![CDATA[").append(map.get("title")).append("]]></Title>").append("<Description><![CDATA[").append(map.get("description")).append("]]></Description>").append("<PicUrl><![CDATA[").append(map.get("picurl")).append("]]></PicUrl>").append("<Url><![CDATA[").append(map.get("url")).append("]]></Url>").append("</item>");
        }
        xml.append("</item></Articles></xml>");
        return xml.toString();
    }

    public static Map<String,String> createArticle(String title,String description,String picurl,String url ){
        Map<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("description",description);
        map.put("picurl",picurl);
        map.put("url",url);
        return map;
    }
}
