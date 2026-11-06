package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushAreaToErp {

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
		
		Expression qualifier = ExpressionFactory.noMatchExp(TMtArea.AREA_LEVEL_PROPERTY, null);
		SelectQuery select = new SelectQuery(TMtArea.class, qualifier);
        List<TMtArea> list = context.performQuery(select);
        
        for (TMtArea tMtArea : list) {
        	/***
        	 *         "LIN": "626b414ada7d218397cc438ee2e7c28f",	密钥
        "OPTYPE": "1",	操作类型1增3更新2删除
        "FNumber": "12345678",	编码
        "FName": "名称",	名称
        "FAREA_ID": "456789",	业务主键
        "FAREA_CODE": "1234",	区域编码
        "FPIC_NAME": "联系人",	联系人
        "FAREA_NAME": "区域名称",	区域名称
        "FPIC_PHONE": "区域类型",	联系人电话
        "FAREA_TYPE": "区域类型",	区域类型
        "FZIP_CODE": "123",	区号
        "FAREA_PIC": "区域负责人",	区域负责人
        "FAREA_DESC": "备注",	备注
        "FAREA_PIC_NAME": "区域负责人姓名"	区域负责人姓名

        	 */
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("LIN", "626b414ada7d218397cc438ee2e7c28f");
        	map.put("OPTYPE", "1");
        	map.put("FNumber", tMtArea.getId().toString());
        	map.put("FName", tMtArea.getAreaName());
        	map.put("FAREA_ID", tMtArea.getId().toString());
        	map.put("FAREA_CODE", tMtArea.getId().toString());
        	map.put("FAREA_NAME", tMtArea.getAreaName());
        	map.put("FAREA_TYPE", tMtArea.getAreaLevel().toString());

        	String json = JSON.toJSONString(map);
        	System.out.println(json);
//			String result = WebUtils.doPost("http://192.168.101.170/KDQuery/api/Query/ImporyArea", WebUtils.DEFAULT_CHARSET,headJson.getBytes(WebUtils.DEFAULT_CHARSET) , WebUtils.connectTimeout,WebUtils.readTimeout);
//			System.out.println(result);
        	 DefaultHttpClient httpclient=new DefaultHttpClient();
             String result="";
             HttpPost httppost = new HttpPost("http://192.168.101.170/KDQuery/api/Query/ImporyArea");
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
             }
             EntityUtils.consume(entity);
             httpclient.getConnectionManager().shutdown();
             System.out.println(result);
			return;
			
		}
        
	}
}
