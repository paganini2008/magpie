package com.github.doodler.common.timeseries;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Description: SimpleRateUpdater
 * @Author: Fred Feng
 * @Date: 22/09/2023
 * @Version 1.0.0
 */
public class SimpleRateUpdater implements RateUpdater {

    private final LongAdder counter;

    public SimpleRateUpdater() {
        this.counter = new LongAdder();
    }

    private volatile long latestCount;
    private volatile int rate;

    @Override
    public void incr() {
        counter.increment();
    }

    @Override
    public int get() {
        return rate;
    }

    @Override
    public void set() {
        long currentCount = counter.longValue();
        if (currentCount > 0) {
            rate = (int) (currentCount - latestCount);
            latestCount = currentCount;
        }
    }
}
