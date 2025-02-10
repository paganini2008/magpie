package com.github.doodler.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @Description: MutableObservable
 * @Author: Fred Feng
 * @Date: 09/01/2025
 * @Version 1.0.0
 */
public class MutableObservable {

    private final ConcurrentMap<String, Observable> channels = new ConcurrentHashMap<>();
    private final boolean repeatable;

    public MutableObservable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void addObserver(MutableObserver ob) {
        addObserver("default", ob);
    }

    public void addObserver(String channel, MutableObserver ob) {
        Observable obs = channels.get(channel);
        if ((obs == null) || (ob.isPrimary())) {
            if (ob.isPrimary()) {
                channels.put(channel, new PrimaryObservable());
            } else {
                channels.putIfAbsent(channel, new DefaultObservable());
            }
            obs = channels.get(channel);
        }
        obs.addObserver(ob);
    }

    public boolean notifyObservers(Object arg) {
        return notifyObservers("default", arg);
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

    private class DefaultObservable extends Observable {

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

    private class PrimaryObservable extends DefaultObservable {

        @Override
        public void addObserver(Observer o) {
            if (super.countObservers() == 0) {
                super.addObserver(o);
            }
        }
    }
}
