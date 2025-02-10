package com.github.doodler.common.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @Description: Logging
 * @Author: Fred Feng
 * @Date: 17/01/2025
 * @Version 1.0.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    OperationType operation() default OperationType.RETRIEVE;

    String desc() default "";
}
