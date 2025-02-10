package com.github.doodler.common.mybatis.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @Description: QueryCondition
 * @Author: Fred Feng
 * @Date: 03/02/2025
 * @Version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface QueryCondition {

    WrapperOp op() default WrapperOp.EQ;

    String field() default "";
}
