package com.github.doodler.common.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: Base
 * @Author: Fred Feng
 * @Date: 19/01/2025
 * @Version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Base {

    OpType opType();

    long threshold() default 1;

    long timeout() default 3;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
