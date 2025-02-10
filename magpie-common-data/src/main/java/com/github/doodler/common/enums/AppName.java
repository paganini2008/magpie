package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @Description: AppName
 * @Author: Fred Feng
 * @Date: 05/01/2024
 * @Version 1.0.0
 */
public enum AppName implements EnumConstant {

    AGGREGATION("doodler-common-service", "common", "/common");

    private final String fullName;
    private final String shortName;
    private final String contextPath;

    private AppName(String fullName, String shortName, String contextPath) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.contextPath = contextPath;
    }

    @Override
    @JsonValue
    public String getValue() {
        return fullName;
    }

    @Override
    public String getRepr() {
        return this.fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @JsonCreator
    public static AppName get(String value) {
        return EnumUtils.valueOf(AppName.class, value);
    }
}
