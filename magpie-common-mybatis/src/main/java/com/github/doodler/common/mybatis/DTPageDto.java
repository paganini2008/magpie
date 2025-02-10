package com.github.doodler.common.mybatis;

import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @Description: DTPageDto
 * @Author: Fred Feng
 * @Date: 03/02/2025
 * @Version 1.0.0
 */
@Data
@ApiModel(description = "Date time Page query dto")
public class DTPageDto extends PageInfo {

    @ApiModelProperty(value = "from time", example = "2022-12-01 10:00:00")
    private String from;

    @ApiModelProperty(value = "to time", example = "2022-12-01 10:00:00")
    private String to;

    public <T> QueryWrapper<T> toQueryWrapper(String fromColumn, String toColumn) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.ge(StringUtils.isNotBlank(from), fromColumn, from);
        queryWrapper.ge(StringUtils.isNotBlank(from), toColumn, from);
        queryWrapper.le(StringUtils.isNotBlank(to), toColumn, to);
        return queryWrapper;
    }
}
