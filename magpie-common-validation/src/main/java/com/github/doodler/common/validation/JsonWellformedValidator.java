package com.github.doodler.common.validation;

import org.apache.commons.lang3.StringUtils;

import com.github.doodler.common.utils.JacksonUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @Description: JsonWellformedValidator
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class JsonWellformedValidator implements ConstraintValidator<JsonWellformed, String> {

    private Class<?> testClass;

    @Override
    public void initialize(JsonWellformed anno) {
        this.testClass = anno.test();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            try {
                JacksonUtils.parseJson(value, testClass);
            } catch (RuntimeException e) {
                return false;
            }
        }
        return true;
    }
}