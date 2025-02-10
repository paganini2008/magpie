package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: RateUpdater
 * @Author: Fred Feng
 * @Date: 21/01/2025
 * @Version 1.0.0
 */
public interface RateUpdater {

    void incr();

    int get();

    void set();
}
