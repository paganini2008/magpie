package com.github.doodler.common.events;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: GlobalApplicationEvent
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
@Getter
@Setter
public abstract class GlobalApplicationEvent {

    protected GlobalApplicationEvent() {
        super();
    }

    protected GlobalApplicationEvent(Object source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    private Object source;
    private long timestamp;

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }

}
