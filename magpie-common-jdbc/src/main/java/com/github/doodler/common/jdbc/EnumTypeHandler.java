package com.github.doodler.common.jdbc;

import com.github.doodler.common.enums.EnumConstant;

/**
 * 
 * @Description: EnumTypeHandler
 * @Author: Fred Feng
 * @Date: 14/01/2025
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class EnumTypeHandler implements TypeHandler {

    @Override
    public boolean support(Object originalValue) {
        return originalValue instanceof Enum;
    }

    @Override
    public Object convertValue(Object originalValue) {
        return originalValue instanceof EnumConstant ? ((EnumConstant) originalValue).getValue()
                : ((Enum) originalValue).ordinal();
    }

}
