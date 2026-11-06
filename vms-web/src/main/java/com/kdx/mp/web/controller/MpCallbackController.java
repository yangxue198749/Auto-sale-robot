package com.kdx.mp.web.controller;

import com.kdx.mp.util.WeChatUtils;
import com.kdx.mp.service.IMpCallbackService;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/22.
 */
@Controller
@RequestMapping("mp/callback")
public class MpCallbackController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IMpCallbackService mpCallbackService;
    @RequestMapping(value="", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String wechatAcceptor(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response, HttpServletRequest request) throws IOException {
        int size = request.getContentLength();
        if(size<=0)return echostr;
        InputStream is = request.getInputStream();
        byte[] reqBodyBytes = WeChatUtils.readBytes(is, size);
        String res = new String(reqBodyBytes);
        Map<String,String> map= WeChatUtils.convertToMap(res);
        logger.debug("微信回调消息"+map.toString());
        String msgType=map.get("MsgType");
        try {
            Class clazz = this.mpCallbackService.getClass();
            Method method = clazz.getDeclaredMethod(msgType,Map.class);
            String xml = (String) method.invoke(this.mpCallbackService,map);
            if(!StringUtil.isEmpty(xml)) return xml;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error("消息类型="+msgType+"==="+e.getLocalizedMessage());
        }
        return echostr;
    }

}
