package com.kdx.vms.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * Created by liyang on 2016/12/20.
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static final String ENCODING_UTF8 = "UTF-8";

    public static String sendFoGet(String url, String context, int timeOut) {
        if (url == null || "".equals(url.trim())) {
            logger.error("url is null.");
            return null;
        }
        if (context == null || "".equals(context.trim())) {
            logger.error("context is null.");
            return null;
        }
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        StringBuilder responseBuilder = new StringBuilder();
        try {
            URL sendUrl = new URL(url);
            URLConnection conn = sendUrl.openConnection();
            logger.info("open connect：" + url);
            conn.setDoOutput(true);
            conn.setConnectTimeout(timeOut);
            writer = new OutputStreamWriter(conn.getOutputStream(), ENCODING_UTF8);
            writer.write(context);
            writer.flush();
            logger.info("url：" + url + " request：" + context + " timeout：" + timeOut);
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), ENCODING_UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            logger.info("response：" + responseBuilder.toString());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return responseBuilder.toString();
    }

}
