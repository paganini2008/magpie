package com.github.doodler.common.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: SharedLock
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SharedLock {

    String prefix() default "";

    /**
     * Support SpEL
     * 
     * @return
     */
    String key() default "";

    boolean fair() default true;

    long duration() default -1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
