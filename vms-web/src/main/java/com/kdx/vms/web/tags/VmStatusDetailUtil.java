package com.kdx.vms.web.tags;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.bean.TVmStatus;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.cache.DictCache;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 解析收货机状态工具类
 * @author zhangguodong
 *
 */
public class VmStatusDetailUtil {

    private static final Logger logger = LoggerFactory.getLogger(SkuUtils.class);

	private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
	
	/**
	 * 解析当前售货机的最新售货机状态
	 * @param vmId
	 * @return
	 */
    public static String getVmStatusDetailStr(Integer vmId){
    	if(null == vmId)return "";
    	String msg = "";
    	try {
    		String  vmStatusDetailStr = getVmStatusDetail(vmId);
    		if(StringUtil.isEmpty(vmStatusDetailStr))return "";
    		msg = formatVmStatus(vmStatusDetailStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
    	return msg;
    }
    /**
     * 解析售货机状态详情
     * @param vmStatusDetailStr
     * @return
     */
	private static String formatVmStatus(String vmStatusDetailStr) {
		String msg = "";
		try {
			JSONObject detailObj = JSON.parseObject(vmStatusDetailStr);
			if(null != detailObj){
				Set<String> set = detailObj.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext()){
					String key = iterator.next();
					if("channelMotorFailureCodes".equals(key)){
						//故障电机特殊处理
						String codes = detailObj.getString(key);
						if(null != codes && codes.length()>0){
							if(codes.startsWith("["))codes = codes.substring(1);
							if(codes.endsWith("]"))codes = codes.substring(0,codes.length()-1);
						}
						String[] list = (null == codes || codes.length()==0)?null:codes.split(",");
						if(null != list && list.length>0){
							for (Object code : list) {
								if(code instanceof Double){
									msg += ((Double) code).intValue()+",";
								}else if(code instanceof String){
									msg += code+",";
								}else if(code instanceof Integer){
									msg += code+",";
								}
							}
							msg += "电机故障,";
						}
					}else if("channelMotorFailureValues".equals(key)){
						String codes = detailObj.getString(key);
						if(null != codes && codes.length()>0){
							if(codes.startsWith("["))codes = codes.substring(1);
							if(codes.endsWith("]"))codes = codes.substring(0,codes.length()-1);
						}
						String[] list = (null == codes || codes.length()==0)?null:codes.split(",");
						if(null != list && list.length>0){
							for (Object code : list) {
								if(code instanceof Double){
									msg += ((Double) code).intValue()+",";
								}else if(code instanceof String){
									msg += code+",";
								}else if(code instanceof Integer){
									msg += code+",";
								}
							}
							msg += "货道故障,";
						}
					}else if("temps".equals(key)){
						
					}else{
						Object value = detailObj.get(key);
							if(null != value){
//								TSysTypeInfo typeInfo = findSysTypeInfo(key);
								TSysTypeInfo typeInfo = DictCache.getDictByCode(key);
								if(null != typeInfo){
//									List<TSysDictionary> sysDictionaryList = findByTypeInfoId(typeInfo.getId());
									List<TSysDictionary> sysDictionaryList = typeInfo.getDictionary();
									if(null != sysDictionaryList && sysDictionaryList.size()>0){
										for (TSysDictionary tSysDictionary : sysDictionaryList) {
											if(value instanceof Double){
												if((Integer.valueOf(((Double)value).intValue())).toString().equals(tSysDictionary.getDictionaryCode())){
													msg += tSysDictionary.getDictionaryName()+",";
													break;
												}
											}else if(value instanceof String){
												if(value.equals(tSysDictionary.getDictionaryCode())){
													msg += tSysDictionary.getDictionaryName()+",";
													break;
												}
											}else if(value instanceof Integer){
												if(value.toString().equals(tSysDictionary.getDictionaryCode())){
													msg += tSysDictionary.getDictionaryName()+",";
													break;
												}
											}else if(value instanceof Boolean){
												if(value.toString().equalsIgnoreCase(tSysDictionary.getDictionaryCode())){
													msg += tSysDictionary.getDictionaryName()+",";
													break;
												}
											}else if(value instanceof BigDecimal){
												if(Integer.valueOf(((BigDecimal) value).intValue() ).toString().equals(tSysDictionary.getDictionaryCode())){
													msg += tSysDictionary.getDictionaryName()+",";
													break;
												}
											}

										}
									}
								}
							}

					}
				}
			}
			if(msg.endsWith(","))msg = msg.substring(0,msg.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return msg;
	}
	/**
	 * 获取解析之后的售货机状态详情
	 * @param message
	 * @return
	 */
    public static String vmStatusDetailStr(String message){
    	return formatVmStatus(message);
    	
    }
//	private static List<TSysDictionary> findByTypeInfoId(Integer typeInfoId) {
//		SelectQuery query = new SelectQuery(TSysDictionary.class ,ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY, typeInfoId));
//		return context.performQuery(query);
//	}
//	private static TSysTypeInfo findSysTypeInfo(String key) {
//		SelectQuery query = new SelectQuery(TSysTypeInfo.class ,ExpressionFactory.expTrue());
//		if(StringUtils.isNotEmpty(key)){
//			query.andQualifier(ExpressionFactory.matchExp(TSysTypeInfo.TYPE_CODE_PROPERTY, key));
//		}
//		List<TSysTypeInfo> list = context.performQuery(query);
//		if(list != null && list.size() > 0) return list.get(0);
//		return null;
//	}
	private static String getVmStatusDetail(Integer vmId) {
		SelectQuery query = new SelectQuery(TVmStatus.class ,ExpressionFactory.matchExp(TVmStatus.MT_VM_PROPERTY, vmId));
		List<TVmStatus> list = context.performQuery(query);
		if(list != null && list.size() > 0) {
			TVmStatus status = list.get(0);
			if(null != status){
				return status.getVmStatusDatail();
			}
		}
		return null;
	}
	public static String getVmStatusStr(Integer vmId){
		try {
			SelectQuery query = new SelectQuery(TVmStatus.class ,ExpressionFactory.matchExp(TVmStatus.MT_VM_PROPERTY, vmId));
			List<TVmStatus> list = context.performQuery(query);
			if(null != list && list.size()>0){
				TVmStatus status = list.get(0);
				Integer vmStatus = status.getVmStatus();
				if(null != vmStatus){
					TSysTypeInfo typeInfo = DictCache.getDictByCode("vmStautsReport");
//					TSysTypeInfo typeInfo = findSysTypeInfo("vmStautsReport");
					if(null != typeInfo){
//						List<TSysDictionary> sysDictionaryList = findByTypeInfoId(typeInfo.getId());
						List<TSysDictionary> sysDictionaryList = typeInfo.getDictionary();
						if(null != sysDictionaryList && sysDictionaryList.size()>0){
							for (TSysDictionary tSysDictionary : sysDictionaryList) {
								if((vmStatus+"").equals(tSysDictionary.getDictionaryCode())){
									return tSysDictionary.getDictionaryName();
								}
							}
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		
		return "<span class = 'icon_state gray'>未知</span>";
	}
	public static String getVmStatus(Integer vmStatus){
		try {
			if(null != vmStatus){
//				TSysTypeInfo typeInfo = findSysTypeInfo("vmStautsReport");
				TSysTypeInfo typeInfo = DictCache.getDictByCode("vmStautsReport");
				if(null != typeInfo){
//					List<TSysDictionary> sysDictionaryList = findByTypeInfoId(typeInfo.getId());
					List<TSysDictionary> sysDictionaryList = typeInfo.getDictionary();
					if(null != sysDictionaryList && sysDictionaryList.size()>0){
						for (TSysDictionary tSysDictionary : sysDictionaryList) {
							if((vmStatus+"").equals(tSysDictionary.getDictionaryCode())){
								return tSysDictionary.getDictionaryName();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		
		return "未知";
	}
	public static String getHisTempValue(String tempValueStr){
		String msg = "";
    	try {
			JSONArray array = JSON.parseArray(tempValueStr);
			if(null != array && array.size()>0){
				for(int i= 0; i<array.size();i++){
					JSONObject obj = array.getJSONObject(i);
					if(obj.containsKey(TMtVm.TEMP_BOX_NO)&&obj.containsKey(TMtVm.TEMP_VALUE)){
						if(null == obj.get(TMtVm.TEMP_BOX_NO) || StringUtil.isEmpty(obj.get(TMtVm.TEMP_BOX_NO)+""))continue;
						msg += obj.getIntValue(TMtVm.TEMP_BOX_NO)+"号:"+obj.getIntValue(TMtVm.TEMP_VALUE)+"<br>";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return msg;
	}
}
