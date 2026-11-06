package com.kdx.vms.web.log;

import java.util.List;
import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

public class LogAdvice {
	
	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@Resource
	private ISysLogService iSysLogService;
	
	private ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private List<TSysDictionary> dictionaryList;
	

	
	
	public Object add_around(ProceedingJoinPoint pjp) throws Throwable{
		Signature signature = pjp.getSignature();
		Object obj = null;
		try {
			obj = pjp.proceed(pjp.getArgs());
			addLog(signature.toShortString(), obj, TSysLog.INSERT);
		} catch (Exception e) {  
			log.error(e.getLocalizedMessage(), e);
		}
		return obj;
	}
	
	public Object update_around(ProceedingJoinPoint pjp) throws Throwable{
		Signature signature = pjp.getSignature();
		Object obj = null;
		try {
			obj = pjp.proceed(pjp.getArgs());
			addLog(signature.toShortString(), obj, TSysLog.UPDATE);
		} catch (Exception e) {  
			log.error(e.getLocalizedMessage(), e);
		}
		return obj;
	}
	
	public Object delete_around(ProceedingJoinPoint pjp) throws Throwable{
		Signature signature = pjp.getSignature();
		Object obj = null;
		try {
			Object[] args = pjp.getArgs();
			obj = pjp.proceed(args);
			if(obj != null && obj instanceof Boolean && ((Boolean)obj) && args.length > 0){
				addLog(signature.toShortString(), args[0], TSysLog.DELETE);
			}
		} catch (Exception e) {  
			log.error(e.getLocalizedMessage(), e);
		}
		return obj;
	}

	private void addLog(String apiNameShort, Object obj, String logOperation) {
		try {
			if(obj != null && apiNameShort.indexOf("Page") == -1){
				TSysDictionary tSysDictionary = null ;
				switch (logOperation) {
				case TSysLog.INSERT:
					if(obj instanceof Integer) tSysDictionary = getDictionary(apiNameShort.substring(apiNameShort.indexOf(".")+7,apiNameShort.indexOf("(")));
					break;
				case TSysLog.UPDATE:
					if(obj instanceof Integer) tSysDictionary = getDictionary(apiNameShort.substring(apiNameShort.indexOf(".")+7,apiNameShort.indexOf("(")));
					break;
				case TSysLog.DELETE:
					tSysDictionary = getDictionary(apiNameShort.substring(apiNameShort.indexOf(".")+7,apiNameShort.indexOf("(")));
					break;
					
				default:
					break;
				}
				if(tSysDictionary != null ){
					iSysLogService.saveSysLog(logOperation+tSysDictionary.getDictionaryDesc()+obj, logOperation, tSysDictionary.getId(), TokenUtil.getToken().getSysUser().getId(), context);
				}
			}
		} catch (Exception e) {
		}
	}
	
	private TSysDictionary getDictionary(String functionName){
		if(dictionaryList == null ){
			TSysTypeInfo tSysTypeInfo = iSysTypeInfoService.findSysTypeinfo(SysTypeinfoEnum.SYS_LOG, context);
			dictionaryList = tSysTypeInfo.getDictionary();
		}
		for (TSysDictionary tSysDictionary : dictionaryList) {
			String dictionaryCode = tSysDictionary.getDictionaryCode();
			if(dictionaryCode.substring(0, dictionaryCode.indexOf("_")).equalsIgnoreCase(functionName)){
				return tSysDictionary;
			}
		}
		return null;
	}
	

	public Object around(ProceedingJoinPoint pjp) throws Throwable{
		Signature signature = pjp.getSignature();
		String apiNameLong  = signature.toLongString();
		String apiNameShort = signature.toShortString();
		
		Object[] args = pjp.getArgs();
		
		long startTime = System.currentTimeMillis();
		
		log.debug(">>>>>>开始执行的 API:\t" + apiNameShort);
		StringBuffer sb=new StringBuffer();
		sb.append("参数:\t");
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(" ");
		}
		log.debug(sb.toString());
		Object obj = null;
		try {
			obj = pjp.proceed(args);
		} catch (Exception e) {  
			log.error(e.getLocalizedMessage(), e);
		}
		long endTime = System.currentTimeMillis();
		long costTime = endTime - startTime;
		log.debug("执行API:"+apiNameShort+"返回的值:\t" + obj);
		log.debug("执行API:"+apiNameShort+"用时:\t" + costTime+"毫秒");
		log.debug("<<<<<<结束执行API:\t" + apiNameShort);
		return obj;
	}

}

