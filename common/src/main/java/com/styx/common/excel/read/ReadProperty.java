package com.styx.common.excel.read;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadProperty {
	/**
	 * EXCEL列序号
	 * @return
	 */
	public int cellIndex();
	
	// 处理常量
	public String enumType() default "";
	
	//验证部分
	public boolean nullable() default true;
	public String regex() default "";
	public int minLength() default -1;
	public int maxLength() default -1;	
	public String max() default "";
	public String min() default "";
	public int[] intEnum() default {};
}
