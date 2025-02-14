package com.github.doodler.common.feign.statistics;

import com.github.doodler.common.enums.AppName;
import com.github.doodler.common.feign.HttpUtils;
import feign.Request;
import feign.Response;
import feign.codec.Decoder;
import static com.github.doodler.common.feign.RestClientConstants.DEFAULT_MAXIMUM_RESPONSE_TIME;
import static com.github.doodler.common.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @Description: StatisticalDecoder
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class StatisticalDecoder implements Decoder {

    private final Decoder delegate;
    private final RestClientStatisticsService statisticsService;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        try {
            return delegate.decode(response, type);
        } finally {
            doStatistics(response);
        }
    }

    private void doStatistics(Response response) {
        Request request = response.request();
        HttpStatus httpStatus = HttpUtils.getHttpStatus(response.status());
        long startTime;
        try {
            startTime = Long.parseLong(
                    HttpUtils.getFirstHeader(request, REQUEST_HEADER_TIMESTAMP));
        } catch (RuntimeException e) {
            startTime = System.currentTimeMillis();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean isOk = httpStatus.is2xxSuccessful();
        
        statisticsService.update("action", request.url(),startTime, sampler -> {
            HttpSample sample = sampler.getSample();
            sample.totalExecutions.increment();
            if (isOk) {
                sample.successExecutions.increment();
            }
            if (elapsedTime > DEFAULT_MAXIMUM_RESPONSE_TIME) {
                sample.slowExecutions.increment();
            }
            sample.accumulatedExecutionTime.add(elapsedTime);
            sample.concurrents.decrement();
        });
        
        String hostName = URI.create(request.requestTemplate().feignTarget().url()).getHost();
    	AppName appName = AppName.get(hostName);
        statisticsService.update("all", appName.getContextPath()+"/**",startTime, sampler -> {
            HttpSample sample = sampler.getSample();
            sample.totalExecutions.increment();
            if (isOk) {
                sample.successExecutions.increment();
            }
            if (elapsedTime > DEFAULT_MAXIMUM_RESPONSE_TIME) {
                sample.slowExecutions.increment();
            }
            sample.accumulatedExecutionTime.add(elapsedTime);
            sample.concurrents.decrement();
        });
    }
}