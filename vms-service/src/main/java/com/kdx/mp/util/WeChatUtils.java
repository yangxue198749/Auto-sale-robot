package com.kdx.mp.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.mp.commons.util.WeChatConfig;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.SHA1Util;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.redis.MyJedisCluster;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/17.
 */
public class WeChatUtils {
    private static Logger logger = LoggerFactory.getLogger(WeChatUtils.class);
    //    private static String ACCESS_TOKEN = null;
    private static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WeChatConfig.APP_ID + "&secret=" + WeChatConfig.SECRET;
    private static String QR_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
    private static String QR_IMG_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    private static String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    private static String OPEN_ID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatConfig.APP_ID + "&secret=" + WeChatConfig.SECRET + "&code=";
    private static String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=";
    private static String SEND_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    private static String WX_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static String WECHAT_REDIRECIT = "#wechat_redirect";
    private static String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=";
    public static String BASE_SCOPE = "snsapi_base";
    public static String USER_SCOPE = "snsapi_userinfo";
    private static String JSAPI_TICKET = null;
    //    private static long failureTimeStamp = 0;
    private static long jsFailureTimeStamp = 0;

    public static MyJedisCluster myJedisCluster;


    public static Map<String, Object> getJsSdkConfig(String url) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        String jsapiTicket = getJsapiTicket();
        String nonceStr = Md5Util.md5(System.currentTimeMillis() + "touch-found-ticket");
        String signature = signature(nonceStr, timestamp, url, jsapiTicket);
        Map<String, Object> config = new HashMap<>();
        config.put("appId", WeChatConfig.APP_ID);
        config.put("timestamp", timestamp);
        config.put("nonceStr", nonceStr);
        config.put("signature", signature);
        return config;
    }

    private static String signature(String nonceStr, String timestamp, String url, String jsapiTicket) {
        StringBuilder builder = new StringBuilder();
        builder.append("jsapi_ticket=").append(jsapiTicket).append("&");
        builder.append("noncestr=").append(nonceStr).append("&");
        builder.append("timestamp=").append(timestamp).append("&");
        builder.append("url=").append(url);
        return SHA1Util.encode(builder.toString());
    }

    public static String getAccessToken() {
        String accessToken = myJedisCluster.hget("wechat", "access_token");
        Long failureTimeStamp = StringUtil.getLongValue(myJedisCluster.hget("wechat", "failureTimeStamp"), 0L);
        if (!StringUtil.isEmpty(accessToken) && System.currentTimeMillis() < failureTimeStamp) return accessToken;
        else {
            return refreshAccessToken();
        }
    }

    private static String getJsapiTicket() {
        if (null != JSAPI_TICKET && System.currentTimeMillis() < jsFailureTimeStamp) return JSAPI_TICKET;
        else {
            refreshJsApiTicket();
        }
        System.out.println(JSAPI_TICKET);
        return JSAPI_TICKET;
    }

    private static void refreshJsApiTicket() {
        String result = "";
        String url = JSAPI_TICKET_URL + getAccessToken() + "&type=jsapi";
        try {
            result = get(url);
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                url = JSAPI_TICKET_URL + accessToken + "&type=jsapi";
                result = get(url);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        String jsapi_ticket = returnJson.getString("ticket");
        long expires_in = returnJson.getLong("expires_in");
        expires_in *= 1000;
        jsFailureTimeStamp = System.currentTimeMillis() + expires_in;
        JSAPI_TICKET = jsapi_ticket;
    }

    public static JSONObject getTemporaryQrTicket(int sceneId, int expireSeconds) {
        JSONObject scene = new JSONObject();
        JSONObject action_info = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("expire_seconds", expireSeconds);
        json.put("action_name", "QR_SCENE");
        scene.put("scene_id", sceneId);
        action_info.put("scene", scene);
        json.put("action_info", action_info);
        String result = "";
        try {
            result = post(QR_TICKET_URL + getAccessToken(), json.toString());
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                result = post(QR_TICKET_URL + accessToken, json.toString());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return JSON.parseObject(result);
    }

    public static JSONObject getFinalQrTicket(int sceneId) {
        JSONObject scene = new JSONObject();
        JSONObject action_info = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("action_name", "QR_LIMIT_SCENE");
        scene.put("scene_id", sceneId);
        action_info.put("scene", scene);
        json.put("action_info", action_info);
        String result = "";
        try {
            result = post(QR_TICKET_URL + getAccessToken(), json.toString());
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                result = post(QR_TICKET_URL + accessToken, json.toString());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        return returnJson;
    }

    public static JSONObject getFinalQrTicket(String sceneStr) {
        JSONObject scene = new JSONObject();
        JSONObject action_info = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("action_name", "QR_LIMIT_STR_SCENE");
        scene.put("scene_str", sceneStr);
        action_info.put("scene", scene);
        json.put("action_info", action_info);
        String result = "";
        try {
            result = post(QR_TICKET_URL + getAccessToken(), json.toString());
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                result = post(QR_TICKET_URL + accessToken, json.toString());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        return returnJson;
    }

    public static String sendMessage(String openId, String eventKey) {
        JSONObject article = new JSONObject();
        article.put("title", eventKey);
        article.put("description", eventKey);
        article.put("url", "http://www.touchfound.com/");
        article.put("picurl", "http://www.touchfound.com/images/banner_index.png");
        return sendMessage(openId, article);
    }

    public static String sendMessage(String openId, JSONObject... articleJsons) {
        String result = "";
        JSONObject json = new JSONObject();
        json.put("touser", openId);
        json.put("msgtype", "news");
        JSONObject news = new JSONObject();
        JSONArray articles = new JSONArray();
        for (JSONObject article : articleJsons) {
            articles.add(article);
        }
        news.put("articles", articles);
        json.put("news", news);
        String url = SEND_MESSAGE_URL + getAccessToken();
        try {
            result = post(url, json.toString());
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                url = SEND_MESSAGE_URL + accessToken;
                result = post(url, json.toString());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return result;
    }

    public static String getAuthUrl(String redirectUrl, String scope, String state) {
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
        }
        return WX_URL + "?appid=" + WeChatConfig.APP_ID + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=" + scope + "&state=" + state + WECHAT_REDIRECIT;
    }

    public static String sendTaskTemplateMessage(String openId, String name, String date, String address) {
        return sendTaskTemplateMessage(openId, "你有新的任务工单", name, date, "请点击详情进行处理", address);
    }

    private static String sendTaskTemplateMessage(String openId, String title, String name, String date, String remarks, String address) {
        JSONObject data = new JSONObject();
        JSONObject first = new JSONObject();
        first.put("value", title);
        first.put("color", "#173177");
        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", name);
        keyword1.put("color", "#173177");
        JSONObject keyword2 = new JSONObject();
        keyword2.put("value", date);
        keyword2.put("color", "#173177");
        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        if (null != remarks && !remarks.equals("")) {
            JSONObject remark = new JSONObject();
            remark.put("value", remarks);
            remark.put("color", "#173177");
            data.put("remark", remark);
        }
        return sendTemplateMessage(openId, WeChatConfig.TEMPLATE_ID, address, data);
    }

    private static String sendTemplateMessage(String openId, String templateId, String address, JSONObject data) {
        String result = "";
        JSONObject json = new JSONObject();
        json.put("touser", openId);
        json.put("template_id", templateId);
        json.put("url", address);
        json.put("data", data);
        String url = SEND_TEMPLATE_MESSAGE_URL + getAccessToken();
        try {
            result = post(url, json.toString());
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                url = SEND_TEMPLATE_MESSAGE_URL + accessToken;
                result = post(url, json.toString());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return result;
    }

    public static String getOpenId(String code) {
        String result = "";
        String url = OPEN_ID_URL + code + "&grant_type=authorization_code";
        try {
            result = get(url);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        if (!returnJson.containsKey("openid")) return null;
        return returnJson.getString("openid");
    }

    public static String getHeadImgUrl(String openId) {
        String url = "";
        if (null == openId) return url;
        JSONObject returnJson = getUserInfo(openId);
        Object openIdObj = returnJson.get("headimgurl");
        if (null == openIdObj) return url;
        url = openIdObj.toString();
        return url;
    }


    private static JSONObject getUserInfo(String openId) {
        String result = "";
        String url = USER_INFO_URL + getAccessToken() + "&openid=" + openId + "&lang=zh_CN";
        try {
            result = get(url);
            if (result.contains("40014") || result.contains("40001")) {
                String accessToken = refreshAccessToken();
                url = USER_INFO_URL + accessToken + "&openid=" + openId + "&lang=zh_CN";
                result = get(url);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        return returnJson;
    }

    public static String getQrImgUrl(String qrTicket) {
        try {
            return QR_IMG_URL + URLEncoder.encode(qrTicket, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String refreshAccessToken() {
        String result = "";
        try {
            result = get(ACCESS_TOKEN_URL);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        JSONObject returnJson = JSON.parseObject(result);
        String access_token = returnJson.getString("access_token");
        long expires_in = returnJson.getLong("expires_in");
        expires_in *= 1000;
        Long failureTimeStamp = System.currentTimeMillis() + expires_in;
        myJedisCluster.hset("wechat", "access_token", access_token);
        myJedisCluster.hset("wechat", "failureTimeStamp", failureTimeStamp + "");
        return access_token;
    }


    public static String get(String url) throws IOException {
        logger.info("调用接口" + url);
        String result = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            result = EntityUtils.toString(entity, "UTF-8");
            System.out.println(result);
            EntityUtils.consume(entity);
            System.out.println(response);
            logger.info(url + "返回消息" + result);
        }
        httpclient.getConnectionManager().shutdown();
        return result;
    }

    public static String post(String url, String json) throws IOException {
        System.out.println(json);
        logger.info("推送消息到" + url + "消息内容" + json);
        String result = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        BasicHttpEntity requestBody = new BasicHttpEntity();
        requestBody.setContent(new ByteArrayInputStream(json.getBytes("UTF-8")));
        requestBody.setContentLength(json.getBytes("UTF-8").length);
        httpPost.setEntity(requestBody);
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            result = EntityUtils.toString(entity, "UTF-8");
            System.out.println(result);
            EntityUtils.consume(entity);
            System.out.println(response);
            logger.info(url + "返回消息" + result);
        }
        httpclient.getConnectionManager().shutdown();
        return result;
    }

    /**
     * 转换简单的xml to map
     *
     * @param xml xml
     * @return map
     */
    public static Map<String, String> convertToMap(String xml) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            if (root != null) {
                NodeList childNodes = root.getChildNodes();
                if (childNodes != null && childNodes.getLength() > 0) {
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node node = childNodes.item(i);
                        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                            map.put(node.getNodeName(), node.getTextContent());
                        }
                    }
                }
            }
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return map;
    }

    public static byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;

            int readLengthThisTime = 0;

            byte[] message = new byte[contentLen];

            try {

                while (readLen != contentLen) {

                    readLengthThisTime = is.read(message, readLen, contentLen
                            - readLen);

                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }

                    readLen += readLengthThisTime;
                }

                return message;
            } catch (IOException e) {
                // Ignore
                // e.printStackTrace();
            }
        }

        return new byte[]{};
    }

    public static void main(String[] args) {
        sendTaskTemplateMessage("oACRPxIup7K3UhRzAofCUqPHx-g0", "维护任务", DateUtils.getTime1(), "http://vms.touchfound.net/mp/mpTask/repair/list");
//        System.out.println(getAccessToken());
//        String result="{\"access_token\":\"ccsl5u5bn7oIN3BCwwOdXsf2pWR4Fx-yt4NiZD9B4rCj7KFgvQ1pUIdIFe5ssyzLiZ00svHQtrAvnehd3HOBMDs5k0DTwctIVZ0oHsBhHgQ\",\"expires_in\":7200,\"refresh_token\":\"4qeHX5NPQDlpDtrA1KYSmE08LNkJ3GMWmKOEuwgPG1H3fg7raV8qER7XPblpwG2a3m4vCm70OAw5fgClp1jTp_5pPz24g21Q15H0Rw_CKpU\",\"openid\":\"oACRPxIup7K3UhRzAofCUqPHx-g0\",\"scope\":\"snsapi_base\"}";
//        JsonObject returnJson=parser.parse(result).getAsJsonObject();
//        System.out.println(returnJson);
//        System.out.println(returnJson.get("openid"));
//        String openId=returnJson.get("openid").getAsString();
    }

}
