package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.WebUtils;
import com.kdx.vms.model.bean.TMtTheme;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmTheme;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 * Created by liyang on 2017/5/4.
 */
@Controller
public class DealVmBoxRequestController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // （重要）不可用此context做提交和改变对象的操作！！！
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static String urLPrefix = (String) PropertiesLoader.map.get("imgUrlPrefix");

    @Resource private IMtVmService mtVmService;

    @RequestMapping(value = "api/themeInfo" ,method = RequestMethod.POST)
    @ResponseBody
    public String themeInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setStatus(600);
            String res = getStreamParam(request.getInputStream(), WebUtils.DEFAULT_CHARSET);
            LOGGER.error("收到消息：" + res);
            if (StringUtil.isEmpty(res)) return null;
            RequestMessage message = JSON.parseObject(res, RequestMessage.class);
            if (null == message) return null;
            String innerCode = message.getInnerCode();
            String key       = message.getKey();
            Long   time      = message.getTime();
            if (StringUtil.isEmpty(innerCode) || StringUtil.isEmpty(key) || null == time) return null;
            TMtVm mtVm = mtVmService.findMtVmByInnerCode(context, innerCode);
            if (null == mtVm) return null;
            String unique = mtVm.getUniqueCode();
            String serverMd5 = Md5Util.md5(unique + innerCode);
            if (!key.equalsIgnoreCase(serverMd5)) {
                LOGGER.error("[themeInfo] 校验失败: uniqueCode:[" + unique + "], serverMd5:[" + serverMd5 + "], key:[" + key + "]");
                return null;
            }
            // 这个方法不会回null
            List<TMtVmTheme> vmThemeList = mtVm.getVmTheme();
            for (TMtVmTheme vmTheme : vmThemeList) {
                Long uTime = vmTheme.getUtime().getTime();
                if (uTime > time) {
                    TMtTheme theme = vmTheme.getTheme();
                    String fileName = theme.getThemeUrl();
                    fileName = fileName.substring(fileName.lastIndexOf("/"));
                    String url = urLPrefix + theme.getThemeUrl();
                    String md5 = theme.getThemeMd5();
                    Long size = theme.getThemeSize();
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setFileName(fileName);
                    responseMessage.setUrl(url);
                    responseMessage.setMd5(md5);
                    responseMessage.setSize(size);
                    responseMessage.setTime(uTime);
                    response.setStatus(200);
                    return JSON.toJSONString(responseMessage);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }

    private String getStreamParam(InputStream in, String encoding) throws IOException {
        BufferedReader rd = null;
        String line;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            rd = new BufferedReader(new InputStreamReader(in, encoding));
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
        } finally {
            if(in != null) in.close();
            if(rd != null) rd.close();
        }
        return stringBuffer.toString();
    }

    private static class RequestMessage {

        private String innerCode;
        private String key;
        private Long   time;

        public String getInnerCode() {
            return innerCode;
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

    }

    private static class ResponseMessage {

        private String fileName;
        private String url;
        private String md5;
        private Long   size;
        private Long   time;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

    }

    public static void main(String[] args) throws Exception {
//        String request = "{\n" +
//                "    \"innerCode\":\"01000067\",\n" +
//                "    \"key\":\"B8F25C0EEF0687F0350A9228453C03D7\",\n" +
//                "    \"time\":" + System.currentTimeMillis() + "\n" +
//                "}";
////        String url = "/api/pushMessage";
//        String url = "/api/themeInfo";
//        String result =	WebUtils.doPost("http://127.0.0.1:8080" + url, WebUtils.DEFAULT_CHARSET, request.getBytes(WebUtils.DEFAULT_CHARSET) , WebUtils.connectTimeout,WebUtils.readTimeout);
//        System.out.println(result);

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtTheme theme = Cayenne.objectForPK(context, TMtTheme.class, 301);
        System.out.println(theme.getUtime().getTime());
    }

}
