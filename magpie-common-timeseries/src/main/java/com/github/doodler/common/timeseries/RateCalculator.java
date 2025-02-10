package com.github.doodler.common.timeseries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;

/**
 * 
 * @Description: RateCalculator
 * @Author: Fred Feng
 * @Date: 21/01/2025
 * @Version 1.0.0
 */
public class RateCalculator<C, D, E extends Metric, T extends Sampler<E>> extends SimpleTimer {

    public RateCalculator(long period, TimeUnit timeUnit,
            SamplerService<C, D, E, T> samplerService) {
        super(period, timeUnit);
        this.samplerService = samplerService;
    }

    private final SamplerService<C, D, E, T> samplerService;
    private final Map<C, Map<D, RateUpdater>> cache = new ConcurrentHashMap<>();

    public void incr(C category, D dimension) {
        incr(category, dimension, SimpleRateUpdater::new);
    }

    public void incr(C category, D dimension, Supplier<RateUpdater> supplier) {
        Map<D, RateUpdater> map = MapUtils.getOrCreate(cache, category, ConcurrentHashMap::new);
        RateUpdater updater = MapUtils.getOrCreate(map, dimension, supplier);
        updater.incr();
    }

    public int get(C category, D dimension) {
        RateUpdater updater = null;
        if (cache.containsKey(category)) {
            updater = cache.get(category).get(dimension);
        }
        return updater != null ? updater.get() : 0;
    }

    @Override
    public boolean change() throws Exception {
        if (cache.size() > 0) {
            for (C category : cache.keySet()) {
                cache.get(category).entrySet().forEach(e -> {
                    e.getValue().set();
                    int rate = e.getValue().get();
                    if (rate > 0) {
                        samplerService.collect(category, e.getKey(), System.currentTimeMillis(),
                                s -> s.getSample().setRate(rate));
                    }
                });
            }
        }
        return true;
    }
}
