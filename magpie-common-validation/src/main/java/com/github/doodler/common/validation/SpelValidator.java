package com.github.doodler.common.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.github.doodler.common.context.ApplicationContextUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @Description: SpelValidator
 * @Author: Fred Feng
 * @Date: 11/12/2022
 * @Version 1.0.0
 */
public class SpelValidator implements ConstraintValidator<SPEL, String> {

    private String expression;

    @Override
    public void initialize(SPEL anno) {
        this.expression = anno.expression();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            String content = expression.replace("??", value);
            ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) ApplicationContextUtils.getBeanFactory();
            return (Boolean) beanFactory.getBeanExpressionResolver().evaluate(content,
                    new BeanExpressionContext(beanFactory, null));
        }
        return true;
    }
}