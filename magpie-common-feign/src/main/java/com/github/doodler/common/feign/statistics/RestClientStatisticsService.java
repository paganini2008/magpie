package com.github.doodler.common.feign.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.timeseries.LoggingOverflowDataHandler;
import com.github.doodler.common.timeseries.OverflowDataHandler;
import com.github.doodler.common.timeseries.RateCalculator;
import com.github.doodler.common.timeseries.Sampler;
import com.github.doodler.common.timeseries.SamplerImpl;
import com.github.doodler.common.timeseries.StringSamplerService;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * @Description: RestClientStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class RestClientStatisticsService extends StringSamplerService<HttpSample>
        implements ManagedBeanLifeCycle {

    public RestClientStatisticsService() {
        this(Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    public RestClientStatisticsService(
            List<OverflowDataHandler<String, String, HttpSample>> dataHandlers) {
        super(5, TimeWindowUnit.MINUTES, 60, dataHandlers);
    }

    private RateCalculator<String, String, HttpSample, Sampler<HttpSample>> rateCalculator;

    @Override
    protected Sampler<HttpSample> getEmptySampler(String category, String dimension,
            long timestampMillis) {
        return new SamplerImpl<HttpSample>(timestampMillis, new HttpSample());
    }

    public void prepare(String category, String dimension, long timestampMillis,
            Consumer<Sampler<HttpSample>> consumer) {
        this.update(category, dimension, timestampMillis, consumer);
    }

    @Override
    public void update(String category, String dimension, long timestampMillis,
            Consumer<Sampler<HttpSample>> consumer) {
        super.update(category, dimension, timestampMillis, consumer);
        rateCalculator.incr(category, dimension);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rateCalculator = new RateCalculator<>(1, TimeUnit.SECONDS, this);
        rateCalculator.start();
    }

    @Override
    public void destroy() throws Exception {
        if (rateCalculator != null) {
            rateCalculator.stop();
        }
    }
}
