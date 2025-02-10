package com.github.doodler.common.jpa;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 
 * @Description: Restrictions
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public abstract class Restrictions {

    public static LogicalFilter juction() {
        return new JunctionFilter();
    }

    public static LogicalFilter disjuction() {
        return new DisjunctionFilter();
    }

    public static LogicalFilter is(boolean type) {
        return type ? new TrueFilter() : new FalseFilter();
    }

    public static <X, T extends Comparable<T>> LogicalFilter lt(SerializedFunction<X, T> sf, T value) {
        return lt(Property.forName(sf), value);
    }

    public static <T extends Comparable<T>> LogicalFilter lt(String alias, String attributeName, T value) {
        return lt(Property.forName(alias, attributeName), value);
    }

    public static <T extends Comparable<T>> LogicalFilter lt(String attributeName, T value) {
        return lt(null, attributeName, value);
    }

    public static <T extends Comparable<T>> LogicalFilter lt(Field<T> field, T value) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThan(expression, value);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter lt(String alias, String attributeName,
                                                             SubQueryBuilder<T> subQuery) {
        return lt(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T extends Comparable<T>> LogicalFilter lt(SerializedFunction<X, T> sf,
                                                                SubQueryBuilder<T> subQuery) {
        return lt(Property.forName(sf), subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter lt(String attributeName, SubQueryBuilder<T> subQuery) {
        return lt(null, attributeName, subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter lt(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThan(expression, subQuery.toSubquery(builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter lt(Field<T> field, Field<T> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThan(expression, otherField.toExpression(model, builder));
        });
    }

    public static <X, T extends Comparable<T>> LogicalFilter gt(SerializedFunction<X, T> sf, T value) {
        return gt(Property.forName(sf), value);
    }

    public static <T extends Comparable<T>> LogicalFilter gt(String alias, String attributeName, T value) {
        return gt(Property.forName(alias, attributeName), value);
    }

    public static <T extends Comparable<T>> LogicalFilter gt(String attributeName, T value) {
        return gt(null, attributeName, value);
    }

    public static <T extends Comparable<T>> LogicalFilter gt(Field<T> field, T value) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThan(expression, value);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter gt(String alias, String attributeName,
                                                             SubQueryBuilder<T> subQuery) {
        return gt(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T extends Comparable<T>> LogicalFilter gt(SerializedFunction<X, T> sf,
                                                                SubQueryBuilder<T> subQuery) {
        return gt(Property.forName(sf), subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter gt(String attributeName, SubQueryBuilder<T> subQuery) {
        return gt(null, attributeName, subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter gt(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThan(expression, subQuery.toSubquery(builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter gt(Field<T> field, Field<T> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThan(expression, otherField.toExpression(model, builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter lte(String alias, String attributeName, T value) {
        return lte(Property.forName(alias, attributeName), value);
    }

    public static <X, T extends Comparable<T>> LogicalFilter lte(SerializedFunction<X, T> sf, T value) {
        return lte(Property.forName(sf), value);
    }

    public static <T extends Comparable<T>> LogicalFilter lte(String attributeName, T value) {
        return lte(null, attributeName, value);
    }

    public static <T extends Comparable<T>> LogicalFilter lte(Field<T> field, T value) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThanOrEqualTo(expression, value);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter lte(String alias, String attributeName,
                                                              SubQueryBuilder<T> subQuery) {
        return lte(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T extends Comparable<T>> LogicalFilter lte(SerializedFunction<X, T> sf,
                                                                 SubQueryBuilder<T> subQuery) {
        return lte(Property.forName(sf), subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter lte(String attributeName, SubQueryBuilder<T> subQuery) {
        return lte(null, attributeName, subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter lte(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThanOrEqualTo(expression, subQuery.toSubquery(builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter lte(Field<T> field, Field<T> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.lessThanOrEqualTo(expression, otherField.toExpression(model, builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter gte(String alias, String attributeName, T value) {
        return gte(Property.forName(alias, attributeName), value);
    }

    public static <X, T extends Comparable<T>> LogicalFilter gte(SerializedFunction<X, T> sf, T value) {
        return gte(Property.forName(sf), value);
    }

    public static <T extends Comparable<T>> LogicalFilter gte(String attributeName, T value) {
        return gte(null, attributeName, value);
    }

    public static <T extends Comparable<T>> LogicalFilter gte(Field<T> field, T value) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThanOrEqualTo(expression, value);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter gte(String alias, String attributeName,
                                                              SubQueryBuilder<T> subQuery) {
        return gte(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T extends Comparable<T>> LogicalFilter gte(SerializedFunction<X, T> sf,
                                                                 SubQueryBuilder<T> subQuery) {
        return gte(Property.forName(sf), subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter gte(String attributeName, SubQueryBuilder<T> subQuery) {
        return gte(null, attributeName, subQuery);
    }

    public static <T extends Comparable<T>> LogicalFilter gte(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThanOrEqualTo(expression, subQuery.toSubquery(builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter gte(Field<T> field, Field<T> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.greaterThanOrEqualTo(expression, otherField.toExpression(model, builder));
        });
    }

    public static LogicalFilter ne(String alias, String attributeName, Object value) {
        return ne(Property.forName(alias, attributeName), value);
    }

    public static <X> LogicalFilter ne(SerializedFunction<X, ?> sf, Object value) {
        return ne(Property.forName(sf), value);
    }

    public static <T> LogicalFilter ne(String alias, String attributeName, SubQueryBuilder<T> subQuery) {
        return ne(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T> LogicalFilter ne(SerializedFunction<X, T> sf, SubQueryBuilder<T> subQuery) {
        return ne(Property.forName(sf), subQuery);
    }

    public static LogicalFilter ne(String attributeName, Object value) {
        return ne(null, attributeName, value);
    }

    public static <T> LogicalFilter ne(String attributeName, SubQueryBuilder<T> subQuery) {
        return ne(null, attributeName, subQuery);
    }

    public static <T> LogicalFilter ne(Field<T> field, Object value) {
        return create(field, (model, expression, builder) -> {
            return builder.notEqual(expression, value);
        });
    }

    public static <T> LogicalFilter ne(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.notEqual(expression, subQuery.toSubquery(builder));
        });
    }

    public static LogicalFilter neProperty(String leftAttributeName, String rightAttributeName) {
        return neProperty(null, leftAttributeName, null, rightAttributeName);
    }

    public static LogicalFilter neProperty(String leftAlias, String leftAttributeName, String rightAlias,
                                           String rightAttributeName) {
        return ne(Property.forName(leftAlias, leftAttributeName), Property.forName(rightAlias, rightAttributeName));
    }

    public static <X, Y> LogicalFilter neProperty(SerializedFunction<X, ?> leftSf, SerializedFunction<Y, ?> rightSf) {
        return ne(Property.forName(leftSf), Property.forName(rightSf));
    }

    public static <X, Y> LogicalFilter ne(Field<X> field, Field<Y> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.notEqual(expression, otherField.toExpression(model, builder));
        });
    }

    public static LogicalFilter eq(String alias, String attributeName, Object value) {
        return eq(Property.forName(alias, attributeName), value);
    }

    public static <X> LogicalFilter eq(SerializedFunction<X, ?> sf, Object value) {
        return eq(Property.forName(sf), value);
    }

    public static <T> LogicalFilter eq(String alias, String attributeName, SubQueryBuilder<T> subQuery) {
        return eq(Property.forName(alias, attributeName), subQuery);
    }

    public static <X, T> LogicalFilter eq(SerializedFunction<X, ?> sf, SubQueryBuilder<T> subQuery) {
        return eq(Property.forName(sf), subQuery);
    }

    public static LogicalFilter eq(String attributeName, Object value) {
        return eq(null, attributeName, value);
    }

    public static <T> LogicalFilter eq(String attributeName, SubQueryBuilder<T> subQuery) {
        return eq(null, attributeName, subQuery);
    }

    public static <T> LogicalFilter eq(Field<T> field, Object value) {
        return create(field, (model, expression, builder) -> {
            return builder.equal(expression, value);
        });
    }

    public static <T> LogicalFilter eq(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            return builder.equal(expression, subQuery.toSubquery(builder));
        });
    }

    public static LogicalFilter eqProperty(String leftAttributeName, String rightAttributeName) {
        return eqProperty(null, leftAttributeName, null, rightAttributeName);
    }

    public static LogicalFilter eqProperty(String leftAlias, String leftAttributeName, String rightAlias,
                                           String rightAttributeName) {
        return eq(Property.forName(leftAlias, leftAttributeName), Property.forName(rightAlias, rightAttributeName));
    }

    public static <X, Y> LogicalFilter eqProperty(SerializedFunction<X, ?> leftSf, SerializedFunction<Y, ?> rightSf) {
        return eq(Property.forName(leftSf), Property.forName(rightSf));
    }

    public static <X, Y> LogicalFilter eq(Field<X> field, Field<Y> otherField) {
        return create(field, (model, expression, builder) -> {
            return builder.equal(expression, otherField.toExpression(model, builder));
        });
    }

    public static LogicalFilter like(String alias, String attributeName, String pattern, char escapeChar) {
        return like(Property.forName(alias, attributeName), pattern, escapeChar);
    }

    public static LogicalFilter like(String attributeName, String pattern, char escapeChar) {
        return like(null, attributeName, pattern, escapeChar);
    }

    public static <X> LogicalFilter like(SerializedFunction<X, String> sf, String pattern, char escapeChar) {
        return like(Property.forName(sf), pattern, escapeChar);
    }

    public static LogicalFilter like(Field<String> field, String pattern, char escapeChar) {
        return create(field, (model, expression, builder) -> {
            return builder.like(expression, pattern, escapeChar);
        });
    }

    public static LogicalFilter notLike(String alias, String attributeName, String pattern, char escapeChar) {
        return notLike(Property.forName(alias, attributeName), pattern, escapeChar);
    }

    public static LogicalFilter notLike(String attributeName, String pattern, char escapeChar) {
        return notLike(null, attributeName, pattern, escapeChar);
    }

    public static <X> LogicalFilter notLike(SerializedFunction<X, String> sf, String pattern, char escapeChar) {
        return notLike(Property.forName(sf), pattern, escapeChar);
    }

    public static LogicalFilter notLike(Field<String> field, String pattern, char escapeChar) {
        return create(field, (model, expression, builder) -> {
            return builder.notLike(expression, pattern, escapeChar);
        });
    }

    public static LogicalFilter like(String alias, String attributeName, String pattern) {
        return like(Property.forName(alias, attributeName), pattern);
    }

    public static LogicalFilter like(String attributeName, String pattern) {
        return like(null, attributeName, pattern);
    }

    public static <X> LogicalFilter like(SerializedFunction<X, String> sf, String pattern) {
        return like(Property.forName(sf), pattern);
    }

    public static LogicalFilter like(Field<String> field, String pattern) {
        return create(field, (model, expression, builder) -> {
            return builder.like(expression, pattern);
        });
    }

    public static LogicalFilter notLike(String alias, String attributeName, String pattern) {
        return notLike(Property.forName(alias, attributeName), pattern);
    }

    public static LogicalFilter notLike(String attributeName, String pattern) {
        return notLike(null, attributeName, pattern);
    }

    public static <X> LogicalFilter notLike(SerializedFunction<X, String> sf, String pattern) {
        return notLike(Property.forName(sf), pattern);
    }

    public static LogicalFilter notLike(Field<String> field, String pattern) {
        return create(field, (model, expression, builder) -> {
            return builder.notLike(expression, pattern);
        });
    }

    public static <T> LogicalFilter in(String alias, String attributeName, SubQueryBuilder<T> subQuery) {
        return in(Property.forName(alias, attributeName), subQuery);
    }

    public static <T> LogicalFilter in(String attributeName, SubQueryBuilder<T> subQuery) {
        return in(null, attributeName, subQuery);
    }

    public static <X, T> LogicalFilter in(SerializedFunction<X, T> sf, SubQueryBuilder<T> subQuery) {
        return in(Property.forName(sf), subQuery);
    }

    public static <T> LogicalFilter in(Field<T> field, SubQueryBuilder<T> subQuery) {
        return create(field, (model, expression, builder) -> {
            CriteriaBuilder.In<Object> in = builder.in(expression);
            return builder.and(in.value(subQuery.toSubquery(builder)));
        });
    }

    public static <T> LogicalFilter in(String alias, String attributeName, Iterable<T> values) {
        return in(Property.forName(alias, attributeName), values);
    }

    public static <T> LogicalFilter in(String attributeName, Iterable<T> values) {
        return in(null, attributeName, values);
    }

    public static <X, T> LogicalFilter in(SerializedFunction<X, T> sf, Iterable<T> values) {
        return in(Property.forName(sf), values);
    }

    public static <T> LogicalFilter in(Field<T> field, Iterable<T> values) {
        return create(field, (model, expression, builder) -> {
            CriteriaBuilder.In<Object> in = builder.in(expression);
            for (Object value : values) {
                in.value(value);
            }
            return builder.and(in);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter between(String alias, String attributeName, T startValue,
                                                                  T endValue) {
        return between(Property.forName(alias, attributeName), startValue, endValue);
    }

    public static <T extends Comparable<T>> LogicalFilter between(String attributeName, T startValue, T endValue) {
        return between(null, attributeName, startValue, endValue);
    }

    public static <X, T extends Comparable<T>> LogicalFilter between(SerializedFunction<X, T> sf, T startValue,
                                                                     T endValue) {
        return between(Property.forName(sf), startValue, endValue);
    }

    public static <T extends Comparable<T>> LogicalFilter between(Field<T> field, T startValue, T endValue) {
        return create(field, (model, expression, builder) -> {
            return builder.between(expression, startValue, endValue);
        });
    }

    public static <T extends Comparable<T>> LogicalFilter between(Field<T> value, String attributeName,
                                                                  String anotherAttributeName) {
        return create(value, (model, expression, builder) -> {
            Property<T> property = Property.forName(null, attributeName);
            Property<T> anotherProperty = Property.forName(null, anotherAttributeName);
            return builder.between(expression, property.toExpression(model, builder), anotherProperty.toExpression(model,
                    builder));
        });
    }

    public static <T extends Comparable<T>> LogicalFilter between(Field<T> field, Field<T> startValue, Field<T> endValue) {
        return create(field, (model, expression, builder) -> {
            return builder.between(expression, startValue.toExpression(model, builder), endValue.toExpression(model,
                    builder));
        });
    }

    public static LogicalFilter notNull(String alias, String attributeName) {
        return notNull(Property.forName(alias, attributeName));
    }

    public static LogicalFilter notNull(String attributeName) {
        return notNull(null, attributeName);
    }

    public static <X> LogicalFilter notNull(SerializedFunction<X, ?> sf) {
        return notNull(Property.forName(sf));
    }

    public static <T> LogicalFilter notNull(Field<T> field) {
        return create(field, (model, expression, builder) -> {
            return builder.isNotNull(expression);
        });
    }

    public static LogicalFilter isNull(String alias, String attributeName) {
        return isNull(Property.forName(alias, attributeName));
    }

    public static LogicalFilter isNull(String attributeName) {
        return isNull(null, attributeName);
    }

    public static <X> LogicalFilter isNull(SerializedFunction<X, ?> sf) {
        return isNull(Property.forName(sf));
    }

    public static <T> LogicalFilter isNull(Field<T> field) {
        return create(field, (model, expression, builder) -> {
            return builder.isNull(expression);
        });
    }

    public static <T> LogicalFilter not(Field<Boolean> field) {
        return create(field, (model, expression, builder) -> {
            return builder.not(expression);
        });
    }

    public static LogicalFilter or(List<Filter> filters) {
        LogicalFilter result = disjuction();
        for (Filter filter : filters) {
            result = result.or(filter);
        }
        return result;
    }

    public static LogicalFilter and(List<Filter> filters) {
        LogicalFilter result = juction();
        for (Filter filter : filters) {
            result = result.and(filter);
        }
        return result;
    }

    public static LogicalFilter exists(SubQueryBuilder<?> queryBuiler) {
        return new ExistsFilter(queryBuiler);
    }

    public static <T> LogicalFilter create(Field<T> field, PredicateBuilder<T> builder) {
        return new FieldFilter<T>(field, builder);
    }

    private static class JunctionFilter extends LogicalFilter {

        public Predicate toPredicate(Model<?> model, CriteriaBuilder builder) {
            return builder.conjunction();
        }

    }

    private static class DisjunctionFilter extends LogicalFilter {

        public Predicate toPredicate(Model<?> model, CriteriaBuilder builder) {
            return builder.disjunction();
        }

    }

    private static class TrueFilter extends LogicalFilter {

        public Predicate toPredicate(Model<?> model, CriteriaBuilder builder) {
            return builder.isTrue(builder.literal(Boolean.TRUE));
        }

    }

    private static class FalseFilter extends LogicalFilter {

        public Predicate toPredicate(Model<?> model, CriteriaBuilder builder) {
            return builder.isFalse(builder.literal(Boolean.FALSE));
        }

    }

}
