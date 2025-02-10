package com.github.doodler.common.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: SerializableTaskTimer
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
public class SerializableTaskTimer extends SimpleTimer {

    public SerializableTaskTimer(long initialDelay, long period, TimeUnit timeUnit) {
        this(initialDelay, period, timeUnit, true);
    }

    public SerializableTaskTimer(long initialDelay, long period, TimeUnit timeUnit,
            boolean quickStart) {
        super(initialDelay, period, timeUnit, quickStart);
    }

    private final List<Runnable> runners = new CopyOnWriteArrayList<>();

    public void addBatch(Runnable r) {
        if (r != null) {
            if (runners.add(r)) {
                log.info("Add SerializableTask: {}", r.getClass());
            }
        }
    }

    public void removeBatch(Runnable r) {
        if (r != null) {
            if (runners.remove(r)) {
                log.info("Remove SerializableTask: {}", r.getClass());
            }
        }
    }

    public int batchSize() {
        return runners.size();
    }

    @Override
    public boolean change() throws Exception {
        if (!runners.isEmpty()) {
            runners.forEach(r -> r.run());
        }
        return true;
    }
}
