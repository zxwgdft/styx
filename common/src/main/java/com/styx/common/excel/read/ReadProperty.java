package com.styx.common.excel.read;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface ReadProperty {
    /**
     * EXCEL列序号
     *
     * @return
     */
    int cellIndex();

    // 处理常量
    String enumType() default "";

    //验证部分
    boolean nullable() default true;

    String regex() default "";

    int minLength() default -1;

    int maxLength() default -1;

    String max() default "";

    String min() default "";

    int[] intEnum() default {};
}
