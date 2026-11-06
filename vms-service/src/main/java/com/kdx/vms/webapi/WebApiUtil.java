package com.kdx.vms.webapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by yangshuxi on 2017/1/19.
 */
public class WebApiUtil {
    private static Logger logger = LoggerFactory.getLogger(WebApiUtil.class);
    public static String post(String json,String url,String dataType) throws IOException {
        logger.info("推送到ERP"+dataType+"信息"+json);
        DefaultHttpClient httpclient=new DefaultHttpClient();
        String result="";
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/json;charset=utf-8");
        BasicHttpEntity requestBody = new BasicHttpEntity();
        requestBody.setContent(new ByteArrayInputStream(json.getBytes("UTF-8")));
        requestBody.setContentLength(json.getBytes("UTF-8").length);
        httppost.setEntity(requestBody);
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            result = EntityUtils.toString(entity, "UTF-8");
            logger.info("ERP返回信息"+result);
        }
        EntityUtils.consume(entity);
        httpclient.getConnectionManager().shutdown();
        return result;
    }
}
