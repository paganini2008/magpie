package com.github.doodler.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: EnumUtils
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public abstract class EnumUtils {

    @SuppressWarnings("unchecked")
    public static <T extends EnumConstant> T[] findAll(Class<T> enumType, String group) {
        List<T> matches = new ArrayList<T>();
        for (T constant : enumType.getEnumConstants()) {
            if (constant.getGroup().equals(group)) {
                matches.add(constant);
            }
        }
        return (T[]) matches.toArray();
    }

    public static <T extends EnumConstant> T valueOf(Class<T> enumType, Object ordinal) {
        for (T constant : enumType.getEnumConstants()) {
            if (ordinal.equals(constant.getValue())) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum  by ordinal '" + ordinal + "' of " + enumType.getCanonicalName());
    }
}