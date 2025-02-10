package com.github.doodler.common.quartz.scheduler;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.Constants;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.cloud.lb.LbRestTemplate;
import com.github.doodler.common.quartz.executor.JobSignature;
import com.github.doodler.common.quartz.executor.RpcJobBean;
import com.github.doodler.common.utils.JacksonUtils;
import com.github.doodler.common.utils.RandomUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: InternalJobDispatcher
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class InternalJobDispatcher implements JobDispatcher {

    @Autowired
    private ApplicationInfoHolder applicationInfoHolder;

    @Autowired
    private LbRestTemplate restTemplate;

    @Autowired
    private DiscoveryClientService discoveryClientService;

    @Override
    public String directCall(String guid, JobSignature jobSignature, long startTime) {
        HttpMethod httpMethod = HttpMethod.valueOf(jobSignature.getMethod());
        HttpHeaders httpHeaders = getHeaders(jobSignature);
        Map<String, Object> payload = Collections.emptyMap();
        if (StringUtils.isNotBlank(jobSignature.getInitialParameter())) {
            try {
                payload = JacksonUtils.parseJson(jobSignature.getInitialParameter(),
                        new TypeReference<Map<String, Object>>() {
                        });
            } catch (RuntimeException ignored) {
            }
        }

        ResponseEntity<String> responseEntity = null;
        try {
            switch (httpMethod) {
                case HttpMethod.GET:
                case HttpMethod.DELETE:
                    responseEntity = restTemplate.exchange(jobSignature.getUrl(), httpMethod,
                            new HttpEntity<>(httpHeaders), String.class, payload);
                    break;
                case HttpMethod.PUT:
                case HttpMethod.POST:
                    responseEntity = restTemplate.exchange(jobSignature.getUrl(), httpMethod,
                            new HttpEntity<Object>(payload, httpHeaders), String.class);
                    break;
                default:
                    throw new UnsupportedOperationException(httpMethod.name());
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (responseEntity == null) {
            return null;
        }
        if (responseEntity.getStatusCode().isError()) {
            if (log.isErrorEnabled()) {
                log.error("Job Executor Response: {}", responseEntity.getBody().toString());
            }
        }
        return responseEntity.getBody().toString();
    }

    @Override
    public String dispatch(String guid, JobSignature jobSignature, long startTime) {
        Collection<ApplicationInfo> applicationInfos = discoveryClientService.getApplicationInfos(
                jobSignature.getJobExecutor());
        if (CollectionUtils.isEmpty(applicationInfos)) {
            throw new IllegalStateException("Unable to find out application info for job executor: " +
                    jobSignature.getJobExecutor());
        }
        ApplicationInfo applicationInfo = IteratorUtils.first(applicationInfos.iterator());
        URI uri = URI.create(String.format("http://%s%s/job/start", jobSignature.getJobExecutor(),
                StringUtils.isNotBlank(applicationInfo.getContextPath()) ? applicationInfo.getContextPath() : ""));

        RpcJobBean rpcJobBean = new RpcJobBean(guid, jobSignature, startTime);
        rpcJobBean.setJobScheduler(applicationInfoHolder.get());
        HttpHeaders httpHeaders = getHeaders(jobSignature);
        ResponseEntity<ApiResult<String>> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.POST,
                    new HttpEntity<Object>(rpcJobBean, httpHeaders),
                    new ParameterizedTypeReference<ApiResult<String>>() {
                    });
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (responseEntity == null) {
            return null;
        }
        if (responseEntity.getStatusCode().isError()) {
            if (log.isErrorEnabled()) {
                log.error("Job Executor Response: {}", responseEntity.getBody().toString());
            }
        }
        return responseEntity.getBody().toString();
    }

    private HttpHeaders getHeaders(JobSignature jobSignature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", RandomUtils.randomChoice(Constants.userAgents));
        if (ArrayUtils.isNotEmpty(jobSignature.getDefaultHeaders())) {
            int index;
            for (String line : jobSignature.getDefaultHeaders()) {
                index = line.indexOf("=");
                if (index > 0) {
                    headers.add(line.substring(0, index), line.substring(index + 1));
                }
            }
        }
        return headers;
    }
}
