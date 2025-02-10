package com.github.doodler.common.context;

import org.springframework.lang.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @Description: WebMvcInterceptor
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public interface WebMvcInterceptor {

    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    }

    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception e) throws Exception {
    }
}
