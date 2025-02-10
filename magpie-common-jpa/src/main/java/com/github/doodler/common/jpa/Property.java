package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.jpa.LambdaUtils.LambdaInfo;

/**
 * 
 * @Description: Property
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public final class Property<T> implements Field<T> {

    private final String alias;
    private final String attributeName;
    private final Class<T> requiredType;

    Property(String alias, String attributeName, Class<T> requiredType) {
        this.alias = StringUtils.isNotBlank(alias) ? alias : Model.ROOT;
        this.attributeName = attributeName;
        this.requiredType = requiredType;
    }

    public Expression<T> toExpression(Model<?> model, CriteriaBuilder builder) {
        Expression<T> expression = model.getAttribute(alias, attributeName);
        if (requiredType != null) {
            return expression.as(requiredType);
        }
        return expression;
    }

    public String toString() {
        return String.format("%s.%s", alias, attributeName);
    }

    public static <T> Property<T> forName(String attributeName) {
        return forName(null, attributeName);
    }

    public static <T> Property<T> forName(String alias, String attributeName) {
        return forName(alias, attributeName, null);
    }

    public static <T> Property<T> forName(String attributeName, Class<T> requiredType) {
        return forName(null, attributeName, requiredType);
    }

    public static <T> Property<T> forName(String alias, String attributeName, Class<T> requiredType) {
        return new Property<T>(alias, attributeName, requiredType);
    }

    public static <X, T> Property<T> forName(SerializedFunction<X, T> sf) {
        return forName(sf, null);
    }

    public static <X, T> Property<T> forName(SerializedFunction<X, T> sf, Class<T> requiredType) {
        LambdaInfo info = LambdaUtils.inspect(sf);
        String alias = TableAlias.get(info.getClassName());
        return new Property<T>(alias, info.getAttributeName(), requiredType);
    }

}
