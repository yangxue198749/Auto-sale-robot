package com.kdx.vms.web.erp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yangshuxi on 2016/12/30.
 */
@Controller
@RequestMapping("api")
public class ErpWebApiController {
//    private static final String KEY_CODE = "key";
//    private static final String MSG_TYPE_CODE = "msgType";
//    private static final String SN_CODE = "sn";
//    private static final String OPERATION_CODE = "operation";
//    private static final String DATA_CODE = "data";
//
//    private static final String KEY = "626b414ada7d218397cc438ee2e7c28f";
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
////    private static JsonParser parser=new JsonParser();
//    @Autowired
//    private WebApiService webApiService;
//    @RequestMapping(value="import", produces = "application/json; charset=utf-8",consumes = "application/json",method = RequestMethod.POST)
//    public @ResponseBody
//    String importInfo(@RequestBody(required = false) String requestJsonStr, HttpServletResponse response, HttpServletRequest request) throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        ReturnJsonObject returnJsonObject=new ReturnJsonObject();
//        if(null==requestJsonStr){
//            returnJsonObject.setError(ReturnErrorCode.NO_REQUEST_JSON);
//            logger.error(returnJsonObject.toJson());
//            return returnJsonObject.toJson();
//        }
//        String requestIp = getIp(request);
//        String erpUrl = ErpConfig.erpUrl;
//        String erpIp = getIpByName(erpUrl);
//        logger.error("requestIp="+requestIp);
//        logger.error("erpIp="+erpIp);
//        if(StringUtil.isEmpty(erpIp) || !requestIp.contains(erpIp)){
//            returnJsonObject.setError(ReturnErrorCode.ACCESS_DENIED);
//            logger.debug(returnJsonObject.toJson());
//            return returnJsonObject.toJson();
//        }
//        logger.info("ERP传入信息"+requestJsonStr);
//        try {
//        JsonObject requestJson = parser.parse(requestJsonStr).getAsJsonObject();
//        JsonElement keyObject = requestJson.get(KEY_CODE);
//        JsonElement msgTypeObject = requestJson.get(MSG_TYPE_CODE);
//        JsonElement snObject = requestJson.get(SN_CODE);
//        JsonElement operationObject = requestJson.get(OPERATION_CODE);
//        JsonElement dataObject = requestJson.get(DATA_CODE);
//        if(null==keyObject||null==msgTypeObject||null==snObject||null==operationObject||null==dataObject){
//            returnJsonObject.setError(ReturnErrorCode.REQUEST_IMPERFECT);
//            logger.error(returnJsonObject.toJson());
//            return returnJsonObject.toJson();
//        }
//
//            String key = keyObject.getAsString();
//            if (!key.equals(KEY)) {
//                returnJsonObject.setError(ReturnErrorCode.ACCESS_DENIED);
//                logger.debug(returnJsonObject.toJson());
//                return returnJsonObject.toJson();
//            }
//            String msgType = msgTypeObject.getAsString();
//            Long sn = snObject.getAsLong();
//            returnJsonObject.setSn(sn);
//            JsonArray data = dataObject.getAsJsonArray();
//            String operation = operationObject.getAsString();
//
//        Class clazz = this.webApiService.getClass();
//        Method method = clazz.getDeclaredMethod(msgType,JsonArray.class,String.class);
//            ReturnErrorCode errorCode = (ReturnErrorCode) method.invoke(this.webApiService,data,operation);
//            returnJsonObject.setError(errorCode);
//            logger.debug(returnJsonObject.toJson());
//            return returnJsonObject.toJson();
//        }catch (Exception e){
//            returnJsonObject.setError(ReturnErrorCode.DATA_INVALIDATE);
//            logger.error(returnJsonObject.toJson());
//            logger.error(e.getLocalizedMessage());
//            return returnJsonObject.toJson();
//        }
//    }
//
//    private String getIp(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if(ip!=null &&!"".equals(ip) && !"".equalsIgnoreCase(ip)){
//            //多次反向代理后会有多个ip值，第一个ip才是真实ip
//            int index = ip.indexOf(",");
//            if(index != -1){
//                return ip.substring(0,index);
//            }else{
//                return ip;
//            }
//        }
//        ip = request.getHeader("X-Real-IP");
//        if(ip!=null &&!"".equals(ip) && !"unKnown".equalsIgnoreCase(ip)){
//            return ip;
//        }
//        return request.getRemoteAddr();
//    }
//
//    private String getIpByName(String name){
//        InetAddress address = null;
//        try {
//            address = InetAddress.getByName(name);
//        } catch (UnknownHostException e) {
//           logger.error("解析域名失败");
//        }
//        return address.getHostAddress();
//    }
}
