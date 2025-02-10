package com.github.doodler.common.tx;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: Tx
 * @Author: Fred Feng
 * @Date: 05/02/2025
 * @Version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Tx {

    String name() default "";

    long timeout() default 60L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
