package com.github.doodler.common.transmitter;

import java.util.concurrent.atomic.AtomicInteger;
import com.github.doodler.common.events.Context;

/**
 * 
 * @Description: NioContext
 * @Author: Fred Feng
 * @Date: 28/01/2025
 * @Version 1.0.0
 */
public final class NioContext extends Context {

    private final AtomicInteger concurrents = new AtomicInteger(0);

    public AtomicInteger getConcurrents() {
        return concurrents;
    }

}
