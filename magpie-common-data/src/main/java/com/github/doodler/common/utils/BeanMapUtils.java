package com.github.doodler.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.convert.ConversionService;
import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: BeanMapUtils
 * @Author: Fred Feng
 * @Date: 14/01/2025
 * @Version 1.0.0
 */
@UtilityClass
public class BeanMapUtils {

    public Map<String, Object> beanToMap(Object obj) {
        return beanToMap(obj, null, true, null);
    }

    public Map<String, Object> beanToMap(Object obj, CaseFormat caseFormat, boolean includeNull,
            String[] excludedProperties) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> beanMap = new LinkedHashMap<>();
        Field[] allFields = FieldUtils.getAllFields(obj.getClass());
        for (Field field : allFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (ArrayUtils.isNotEmpty(excludedProperties)
                    && ArrayUtils.contains(excludedProperties, field.getName())) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (includeNull || value != null) {
                    beanMap.put(caseFormat != null
                            ? CaseFormat.LOWER_CAMEL.to(caseFormat, field.getName())
                            : field.getName(), value);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return beanMap;
    }

    public <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        return mapToBean(map, null, clazz, null);
    }

    public <T> T mapToBean(Map<String, ?> map, CaseFormat caseFormat, Class<T> clazz,
            ConversionService conversionService) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = ConstructorUtils.invokeConstructor(clazz);
            Field[] fields = FieldUtils.getAllFields(clazz);
            String key;
            Object value;
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                key = caseFormat != null ? CaseFormat.LOWER_CAMEL.to(caseFormat, field.getName())
                        : field.getName();
                value = map.get(key);
                if (value != null) {
                    try {
                        value = field.getType().cast(value);
                    } catch (ClassCastException e) {
                        if (conversionService != null) {
                            try {
                                value = conversionService.convert(value, field.getType());
                            } catch (RuntimeException ee) {
                                value = ConvertUtils.convert(value, field.getType());
                            }
                        } else {
                            value = ConvertUtils.convert(value, field.getType());
                        }
                    }
                    field.setAccessible(true);
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return obj;
    }

}
