package com.kdx.vms.web.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kdx.vms.commons.util.SyslogEnum;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AddLog {
	String logDesc() default "";
	
	String logOperation() default "";
	
	SyslogEnum syslogEnum() ;
}
