package com.github.doodler.common.events;

/**
 * 
 * @Description: GlobalApplicationEventListener
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
public interface GlobalApplicationEventListener<E extends GlobalApplicationEvent> {

    void onGlobalApplicationEvent(E event);

}
