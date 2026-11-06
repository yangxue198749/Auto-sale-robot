package com.kdx.vms.commons.util.sendMessage;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UcPaasUtil {

	private final static Logger logger = LoggerFactory.getLogger(UcPaasUtil.class);

	public static boolean isTest  = false;
	public static String  server  = "api.ucpaas.com";
	public static String  sslIP   = "0";
	public static int     sslPort = 0;
	public static String  version = "2014-06-30";
	
	public static String templateSMS(String accountSid,
                              String authToken,
                              String appId,
                              String templateId,
                              String to,
                              String param) {
		String result = "";
		DefaultHttpClient httpclient = getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = UcPaasUtil.dateToStr(new Date(),	UcPaasUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature = getSignature(accountSid, authToken, timestamp, encryptUtil);
			String url = getStringBuffer().append("/").append(version)
					.append("/Accounts/").append(accountSid)
					.append("/Messages/templateSMS")
					.append("?sig=").append(signature).toString();
			TemplateSMS templateSMS = new TemplateSMS();
			templateSMS.setAppId(appId);
			templateSMS.setTemplateId(templateId);
			templateSMS.setTo(to);
			templateSMS.setParam(param);
			String body = JSON.toJSONString(templateSMS);
			body = "{\"templateSMS\":"+body+"}";
			logger.info(body);
			HttpResponse response = post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
                logger.info("result:" + result);
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
    private static HttpResponse post(String cType,
                             String accountSid,
                             String authToken,
                             String timestamp,
                             String url,
                             DefaultHttpClient httpclient,
                             EncryptUtil encryptUtil,
                             String body) throws Exception {
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Accept", cType);
		httppost.setHeader("Content-Type", cType+";charset=utf-8");
		String src = accountSid + ":" + timestamp;
		String auth = encryptUtil.base64Encoder(src);
		httppost.setHeader("Authorization", auth);
		BasicHttpEntity requestBody = new BasicHttpEntity();
        requestBody.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
        requestBody.setContentLength(body.getBytes("UTF-8").length);
        httppost.setEntity(requestBody);
        // 执行客户端请求
		return httpclient.execute(httppost);
	}
	
	private static String getSignature(String accountSid, String authToken, String timestamp, EncryptUtil encryptUtil) throws Exception{
		String sig = accountSid + authToken + timestamp;
		return encryptUtil.md5Digest(sig);
	}
	
	private static StringBuffer getStringBuffer() {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(server);
		return sb;
	}
	private static DefaultHttpClient getDefaultHttpClient(){
		DefaultHttpClient httpclient=null;
		if (isTest) {
			try {
				SSLHttpClient chc = new SSLHttpClient();
				httpclient = chc.registerSSL(sslIP,"TLS",sslPort,"https");
				HttpParams hParams = new BasicHttpParams();
				hParams.setParameter("https.protocols", "SSLv3,SSLv2Hello");
				httpclient.setParams(hParams);
			} catch (KeyManagementException | NoSuchAlgorithmException  e) {
                logger.error(e.getLocalizedMessage(), e);
			}
		}else {
			httpclient = new DefaultHttpClient();
		}
		return httpclient;
	}

	/**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd
     */
	public static final int DEFAULT = 0;
	public static final int YM = 1;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd
     * 
     */
    public static final int YMR_SLASH = 11;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMdd
     * 
     */
    public static final int NO_SLASH = 2;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMM
     * 
     */
    public static final int YM_NO_SLASH = 3;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm:ss
     * 
     */
    public static final int DATE_TIME = 4;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMddHHmmss
     * 
     */
    public static final int DATE_TIME_NO_SLASH = 5;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm
     * 
     */
    public static final int DATE_HM = 6;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm:ss
     * 
     */
    public static final int TIME = 7;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm
     * 
     */
    public static final int HM = 8;
    
    /**
     * 变量：日期格式化类型 - 格式:HHmmss
     * 
     */
    public static final int LONG_TIME = 9;
    /**
     * 变量：日期格式化类型 - 格式:HHmm
     * 
     */
    
    public static final int SHORT_TIME = 10;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH:mm:ss
     */
    public static final int DATE_TIME_LINE = 12;

	private static String dateToStr(Date date, int type) {
        switch (type) {
        case DEFAULT:
            return dateToStr(date);
        case YM:
            return dateToStr(date, "yyyy/MM");
        case NO_SLASH:
            return dateToStr(date, "yyyyMMdd");
        case YMR_SLASH:
        	return dateToStr(date, "yyyy-MM-dd");
        case YM_NO_SLASH:
            return dateToStr(date, "yyyyMM");
        case DATE_TIME:
            return dateToStr(date, "yyyy/MM/dd HH:mm:ss");
        case DATE_TIME_NO_SLASH:
            return dateToStr(date, "yyyyMMddHHmmss");
        case DATE_HM:
            return dateToStr(date, "yyyy/MM/dd HH:mm");
        case TIME:
            return dateToStr(date, "HH:mm:ss");
        case HM:
            return dateToStr(date, "HH:mm");
        case LONG_TIME:
            return dateToStr(date, "HHmmss");
        case SHORT_TIME:
            return dateToStr(date, "HHmm");
        case DATE_TIME_LINE:
            return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
        default:
            throw new IllegalArgumentException("Type undefined : " + type);
        }
    }

	private static String dateToStr(Date date, String pattern) {
	       if (date == null || null == pattern || pattern.length() == 0) return null;
	       SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	       return formatter.format(date);
    } 

    private static String dateToStr(Date date) {
        return dateToStr(date, "yyyy/MM/dd");
    }
}
