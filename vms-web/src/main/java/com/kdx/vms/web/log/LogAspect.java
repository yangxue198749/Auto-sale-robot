package com.kdx.vms.web.log;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdx.vms.commons.util.SysTypeinfoEnum;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ISysTypeInfoService;
import com.kdx.vms.web.tags.TokenUtil;

@Aspect    
@Component
public class LogAspect {

	@Resource
	private ISysTypeInfoService iSysTypeInfoService;
	
	@Resource
	private ISysLogService iSysLogService;
	
	private ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private List<TSysDictionary> dictionaryList;
	
	
	
	@Pointcut("execution(* com.kdx.vms.web.controller.*.*(..)) ")    
    public  void controllerAspect() {    
    }  
	
	@SuppressWarnings("unused")
	@Around("controllerAspect() && @annotation(addLog)")
	public Object around(ProceedingJoinPoint pjp , AddLog addLog) throws Throwable{
		Signature signature = pjp.getSignature();
		String logDesc = addLog.logDesc();
		String logOperation = addLog.logOperation();
		SyslogEnum syslogEnum = addLog.syslogEnum();
		Object obj = null;
		try {
			obj = pjp.proceed(pjp.getArgs());
			addLog(logDesc, logOperation, syslogEnum, obj);
		} catch (Exception e) {  
			log.error(e.getLocalizedMessage(), e);
		}
		return obj;
	}
	
	public void addLog(String logDesc ,String logOperation ,SyslogEnum syslogEnum ,Object obj){
		if(obj != null ){
			Object id = null ;
			if(obj instanceof Map && ((Map<String,Object>)obj).get("type").equals("success")){
				id = ((Map<String,Object>)obj).get("id");
				iSysLogService.saveSysLog(logOperation+logDesc+(id!=null?id:""), logOperation, syslogEnum, TokenUtil.getToken().getSysUser().getId(), context);
			}
		}
	}
	
}
