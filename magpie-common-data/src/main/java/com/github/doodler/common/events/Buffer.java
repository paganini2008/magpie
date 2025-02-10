package com.github.doodler.common.events;

import java.util.Collection;

/**
 * 
 * @Description: Buffer
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */
public interface Buffer<T> {

    void put(T item);

    long size();

    T poll();

    Collection<T> poll(long fetchSize);

    default void destroy() {}

}
