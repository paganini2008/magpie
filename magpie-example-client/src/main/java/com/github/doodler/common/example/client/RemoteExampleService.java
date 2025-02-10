package com.github.doodler.common.example.client;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.feign.RestClient;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: RemoteExampleService
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@RestClient(serviceId = "doodler-example-service")
public interface RemoteExampleService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("GET /upms/example/{id}")
    public ApiResult<ExampleVo> getExampleById(@Param("id") Integer id);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /upms/example/save")
    public ApiResult<ExampleVo> saveExample(ExampleDto dto);
}
