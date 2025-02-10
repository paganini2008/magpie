package com.github.doodler.common.mybatis;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: SortInfo
 * @Author: Fred Feng
 * @Date: 03/02/2025
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortInfo {

    @ApiModelProperty("Sort Key")
    private String key;
    @ApiModelProperty("Sort type, ASC, DESC")
    private String dir;
}
