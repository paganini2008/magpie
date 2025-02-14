package com.github.doodler.common.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.doodler.common.security.WhiteListProperties.Mode;

/**
 * @Description: WhiteList
 * @Author: Fred Feng
 * @Date: 19/11/2022
 * @Version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WhiteList {

    Mode mode() default Mode.EXTERNAL;
}