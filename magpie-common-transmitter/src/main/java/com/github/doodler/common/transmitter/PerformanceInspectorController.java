package com.github.doodler.common.transmitter;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.doodler.common.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @Description: PerformanceInspectorController
 * @Author: Fred Feng
 * @Date: 21/01/2025
 * @Version 1.0.0
 */
@Api(tags = "Performance Inspector API", hidden = true)
@RequestMapping("/sys/transmitter")
@RestController
public class PerformanceInspectorController {

    @Autowired
    private PerformanceInspectorService performanceInspectorService;

    @ApiOperation(value = "Retrive categories", notes = "Retrive categories", hidden = true)
    @GetMapping("/instances")
    public ApiResult<Collection<String>> categories() {
        return ApiResult.ok(performanceInspectorService.categories());
    }

    @ApiOperation(value = "Retrive time series data by instanceId and mode",
            notes = "Retrive time series data by instanceId and mode", hidden = true)
    @GetMapping("/sequence")
    public ApiResult<Map<String, Object>> sequence(@RequestParam("instanceId") String instanceId,
            @RequestParam("mode") String mode) {
        return ApiResult.ok(performanceInspectorService.sequence(instanceId, mode,
                DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @ApiOperation(value = "Retrive statistical data of all time ranges by instanceId and mode",
            notes = "Retrive statistical data of all time ranges by instanceId and mode",
            hidden = true)
    @GetMapping("/summarize")
    public ApiResult<Object> summarize(@RequestParam("instanceId") String instanceId,
            @RequestParam("mode") String mode) {
        return ApiResult.ok(
                performanceInspectorService.summarize(instanceId, mode).getSample().represent());
    }

}
