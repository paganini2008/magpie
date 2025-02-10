package com.github.doodler.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @Description: SingleObservable
 * @Author: Fred Feng
 * @Date: 14/01/2025
 * @Version 1.0.0
 */
public class SingleObservable {

    private final ConcurrentMap<String, Observable> channels = new ConcurrentHashMap<>();
    private final boolean repeatable;

    public SingleObservable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public boolean addObserver(String channel, Observer ob) {
        Observable obs = channels.get(channel);
        if (obs != null) {
            return false;
        }
        obs = MapUtils.getOrCreate(channels, channel, InternalObservable::new);
        obs.addObserver(ob);
        return true;
    }

    public boolean hasChannel(String channel) {
        return channels.containsKey(channel);
    }

    public boolean notifyObservers(String channel, Object arg) {
        Observable obs = channels.get(channel);
        if (obs != null && obs.countObservers() > 0) {
            obs.notifyObservers(arg);
            return true;
        }
        return false;
    }

    public boolean notifyObservers(String channel, MatchMode matchMode, Object arg) {
        if (channels.entrySet().stream().anyMatch(e -> matchMode.matches(channel, e.getKey()))) {
            channels.entrySet().stream().filter(e -> matchMode.matches(channel, e.getKey()))
                    .forEach(e -> {
                        Observable obs = e.getValue();
                        if (obs != null && obs.countObservers() > 0) {
                            obs.notifyObservers(arg);
                        }
                    });
            return true;
        }
        return false;
    }

    public int countOfChannels() {
        return channels.size();
    }

    public int countObservers() {
        int n = 0;
        for (Observable obs : channels.values()) {
            n += obs.countObservers();
        }
        return n;
    }

    public void clear() {
        channels.clear();
    }

    /**
     * 
     * @Description: DefaultObservable
     * @Author: Fred Feng
     * @Date: 14/01/2025
     * @Version 1.0.0
     */
    private class InternalObservable extends Observable {

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
            if (!repeatable) {
                clearChanged();
                deleteObservers();
            }
        }
    }

}
