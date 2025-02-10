package com.github.doodler.common.events;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Description: Context
 * @Author: Fred Feng
 * @Date: 26/01/2025
 * @Version 1.0.0
 */
public class Context {

    private final Map<String, Object> attrs = new HashMap<>();

    public void setAttr(String name, Object value) {
        if (value != null) {
            attrs.put(name, value);
        } else {
            attrs.remove(name);
        }
    }

    public Object getAttr(String name) {
        return getAttr(name, null);
    }

    public Object getAttr(String name, Object defaultValue) {
        return attrs.getOrDefault(name, defaultValue);
    }
}
