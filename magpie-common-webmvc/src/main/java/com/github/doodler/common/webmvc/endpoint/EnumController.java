package com.github.doodler.common.webmvc.endpoint;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.webmvc.EnumDeclarations;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: EnumController
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@Api(tags = "Enum common API", hidden = true)
@RestController
public class EnumController {

    @ApiOperation(value = "Get enumeration list of current application",
            notes = "Get enumeration list of current application", hidden = true)
    @GetMapping("/enums")
    public ApiResult<Map<String, Map<String, Object>>> getEnums() {
        return ApiResult.ok(EnumDeclarations.getEnums());
    }
}
