package com.kdx.mp.updateMenu;

import com.alibaba.fastjson.JSONObject;
import com.kdx.mp.util.WeChatUtils;

import java.io.IOException;

/**
 * Created by yangshuxi on 2017/3/15.
 */
public class UpdateMenu {

    public static void main(String[] args) throws IOException {
//        updateMenuOnLine();
        updateMenuTest();
    }

    public static void updateMenuTest() throws IOException {
        String appId = "wxa6a2e53865288897";
        String appsecret = "c26615b679f6cce9fc64365d0a5ed4ec";
        String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appsecret;
        String result = WeChatUtils.get(ACCESS_TOKEN_URL);
        JSONObject returnJson= JSONObject.parseObject(result);
        String access_token=returnJson.getString("access_token");
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+access_token;
        String json = "{\n" +
                "    \"button\": [\n" +
                "        {\n" +
                "            \"name\": \"资讯中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"魔核系统\",\n" +
                "                    \"key\": \"text1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"合作模式\",\n" +
                "                    \"key\": \"text2\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"联系我们\",\n" +
                "                    \"key\": \"text3\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"运营中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"scancode_push\",\n" +
                "                    \"name\": \"激活售货机\",\n" +
                "                    \"key\": \"rselfmenu_0_1\",\n" +
                "                    \"sub_button\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"设备状态\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/vmStatus/list\"\n" +
                "                },\n" +
                "\t\t{\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"出货异常\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/outSkuError/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"我的工单\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/mpTask/list\"\n" +
                "                },\n" +
                "\t\t{\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"立即补货\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/delivery/list\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"数据中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"销售流水\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/slOrder/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"销售报表\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/slReport/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"个人主页\",\n" +
                "                    \"url\": \"http://vms.touchfound.net/mp/home\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        WeChatUtils.post(url,json);
    }
    public static void updateMenuOnLine() throws IOException {
        String appId="wxd4475a6e9d648614";
        String appsecret="681213adf19b8c2f511ab3b0f6e744d4";
        String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appsecret;
        String result = WeChatUtils.get(ACCESS_TOKEN_URL);
        JSONObject returnJson= JSONObject.parseObject(result);
        String access_token=returnJson.getString("access_token");
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+access_token;
        String json = "{\n" +
                "    \"button\": [\n" +
                "        {\n" +
                "            \"name\": \"资讯中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"魔核系统\",\n" +
                "                    \"key\": \"text1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"合作模式\",\n" +
                "                    \"key\": \"text2\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"click\",\n" +
                "                    \"name\": \"联系我们\",\n" +
                "                    \"key\": \"text3\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"运营中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"scancode_push\",\n" +
                "                    \"name\": \"激活售货机\",\n" +
                "                    \"key\": \"rselfmenu_0_1\",\n" +
                "                    \"sub_button\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"设备状态\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/vmStatus/list\"\n" +
                "                },\n" +
                "\t\t{\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"出货异常\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/outSkuError/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"我的工单\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/mpTask/list\"\n" +
                "                },\n" +
                "\t\t{\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"立即补货\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/delivery/list\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"数据中心\",\n" +
                "            \"sub_button\": [\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"销售流水\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/slOrder/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"销售报表\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/slReport/list\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"view\",\n" +
                "                    \"name\": \"个人主页\",\n" +
                "                    \"url\": \"http://vms.touchfound.com/mp/home\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        WeChatUtils.post(url,json);
    }
}
