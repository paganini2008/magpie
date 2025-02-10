package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaQuery;
import com.github.doodler.common.jpa.LambdaUtils.LambdaInfo;

/**
 * 
 * @Description: JpaQuery
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface JpaQuery<E, T> {

    JpaQuery<E, T> filter(Filter filter);

    JpaQuery<E, T> sort(JpaSort... sorts);

    default JpaGroupBy<E, T> groupBy(String... attributeNames) {
        return groupBy(new FieldList().addFields(attributeNames));
    }

    default JpaGroupBy<E, T> groupBy(String alias, String[] attributeNames) {
        return groupBy(new FieldList().addFields(alias, attributeNames));
    }

    default JpaGroupBy<E, T> groupBy(Field<?>... fields) {
        return groupBy(new FieldList(fields));
    }

    JpaGroupBy<E, T> groupBy(FieldList fieldList);

    JpaQueryResultSet<T> selectThis();

    JpaQueryResultSet<T> selectAlias(String... tableAliases);

    default JpaQueryResultSet<T> select(String... attributeNames) {
        return select(new ColumnList().addColumns(attributeNames));
    }

    default JpaQueryResultSet<T> select(String alias, String[] attributeNames) {
        return select(new ColumnList().addColumns(alias, attributeNames));
    }

    default JpaQueryResultSet<T> select(Column... columns) {
        return select(new ColumnList(columns));
    }

    default JpaQueryResultSet<T> select(Field<?>... fields) {
        return select(new ColumnList().addColumns(fields));
    }

    JpaQueryResultSet<T> select(ColumnList columnList);

    default T one(String attributeName) {
        return one(Column.forName(attributeName));
    }

    default T one(String alias, String attributeName) {
        return one(Column.forName(alias, attributeName));
    }

    default T one(Field<T> field) {
        return one(field.as(field.toString()));
    }

    T one(Column column);

    JpaQuery<E, T> distinct(boolean distinct);

    <X> JpaQuery<X, T> join(String attributeName, String alias, Filter on);

    <X> JpaQuery<X, T> leftJoin(String attributeName, String alias, Filter on);

    <X> JpaQuery<X, T> rightJoin(String attributeName, String alias, Filter on);

    default <X> JpaQuery<X, T> join(SerializedFunction<X, T> sf, String alias, Filter on) {
        LambdaInfo info = LambdaUtils.inspect(sf);
        return join(info.getAttributeName(), alias, on);
    }

    default <X> JpaQuery<X, T> leftJoin(SerializedFunction<X, T> sf, String alias, Filter on) {
        LambdaInfo info = LambdaUtils.inspect(sf);
        return leftJoin(info.getAttributeName(), alias, on);
    }

    default <X> JpaQuery<X, T> rightJoin(SerializedFunction<X, T> sf, String alias, Filter on) {
        LambdaInfo info = LambdaUtils.inspect(sf);
        return rightJoin(info.getAttributeName(), alias, on);
    }

    <X> JpaSubQuery<X, X> subQuery(Class<X> entityClass, String alias);

    <X, Y> JpaSubQuery<X, Y> subQuery(Class<X> entityClass, String alias, Class<Y> resultClass);

    CriteriaQuery<T> query();

    Model<E> model();

}
