/**
 * Copyright 2017-2025 Fred Feng (paganini.fy@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.doodler.common.jdbc;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import com.github.doodler.common.context.ApplicationContextUtils;
import com.github.doodler.common.jdbc.annotations.Arg;
import com.github.doodler.common.jdbc.annotations.Args;
import com.github.doodler.common.jdbc.annotations.Batch;
import com.github.doodler.common.jdbc.annotations.Example;
import com.github.doodler.common.jdbc.annotations.Get;
import com.github.doodler.common.jdbc.annotations.Insert;
import com.github.doodler.common.jdbc.annotations.PageQuery;
import com.github.doodler.common.jdbc.annotations.Query;
import com.github.doodler.common.jdbc.annotations.Sql;
import com.github.doodler.common.jdbc.annotations.Update;
import com.github.doodler.common.page.PageReader;
import com.github.doodler.common.page.PageRequest;
import com.github.doodler.common.page.PageResponse;
import com.github.doodler.common.page.PageVo;
import com.github.doodler.common.utils.BeanMapUtils;
import com.github.doodler.common.utils.ConvertUtils;
import com.github.doodler.common.utils.LogUtils;
import com.github.doodler.common.utils.MapUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DaoProxyBean
 * @Author: Fred Feng
 * @Date: 31/08/2024
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class DaoProxyBean<T> extends EnhancedNamedParameterJdbcDaoSupport
        implements InvocationHandler {

    private final Class<T> interfaceClass;
    private final List<TypeHandler> typeHandlers;
    protected final Logger log;

    DaoProxyBean(DataSource dataSource, Class<T> interfaceClass, List<TypeHandler> typeHandlers) {
        Assert.notNull(dataSource, "DataSource must be required.");
        this.setDataSource(dataSource);
        this.interfaceClass = interfaceClass;
        this.typeHandlers = typeHandlers;
        this.log = LoggerFactory.getLogger(interfaceClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Insert.class)) {
            return doInsert(method, args);
        } else if (method.isAnnotationPresent(Update.class)) {
            return doUpdate(method, args);
        } else if (method.isAnnotationPresent(Get.class)) {
            return doGet(method, args);
        } else if (method.isAnnotationPresent(Query.class)) {
            return doQuery(method, args);
        } else if (method.isAnnotationPresent(PageQuery.class)) {
            return doPageQuery(method, args);
        } else if (method.isAnnotationPresent(Batch.class)) {
            return doBatch(method, args);
        }
        throw new NotImplementedException(
                "Unknown target method: " + interfaceClass.getName() + "." + method.getName());
    }

    private Class<?> getMethodReturnTypeElementType(Method method) {
        Type returnType = method.getGenericReturnType();
        ParameterizedType parameterizedType = (ParameterizedType) returnType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type firstType = actualTypeArguments[0];
        if (firstType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) firstType).getRawType();
        } else if (firstType instanceof Class) {
            return (Class<?>) firstType;
        }
        throw new UnsupportedOperationException(returnType.getTypeName());
    }

    private Object doQuery(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException("Return type is only for List");
        }
        Query select = method.getAnnotation(Query.class);
        Class<?> elementType = getMethodReturnTypeElementType(method);
        StringBuilder sqlBuilder = new StringBuilder(select.value());
        SqlParameterSource sqlParameterSource =
                getSqlParameterSource(method, args, sqlBuilder, null);

        String actualSql = getActualSql(sqlBuilder.toString(), sqlParameterSource);
        Object[] actualArgs = getActualArgs(sqlParameterSource);
        for (Class<?> listenerClass : select.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        String sql = sqlBuilder.toString();
        Object result = null;
        Exception thrown = null;
        try {
            if (select.singleColumn()) {
                return result = getNamedParameterJdbcTemplate().queryForList(sql,
                        sqlParameterSource, elementType);
            } else {
                if (Map.class.isAssignableFrom(elementType)) {
                    return result = getNamedParameterJdbcTemplate().query(sql, sqlParameterSource,
                            new NamedColumnMapRowMapper());
                } else {
                    return result = getNamedParameterJdbcTemplate().query(sql, sqlParameterSource,
                            new BeanPropertyRowMapper<>(elementType));
                }
            }
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : select.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, 0, result, thrown);
        }
    }

    private Object doPageQuery(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        if (!PageReader.class.isAssignableFrom(method.getReturnType())
                && !PageVo.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException("Return type is only for PageReader or PageVo");
        }
        Class<?> elementType = getMethodReturnTypeElementType(method);
        final PageQuery query = method.getAnnotation(PageQuery.class);
        StringBuilder countSqlBuilder = new StringBuilder(query.countSql());
        StringBuilder sqlBuilder = new StringBuilder(query.value());
        SqlParameterSource sqlParameterSource =
                getSqlParameterSource(method, args, sqlBuilder, countSqlBuilder);

        String actualSql = getActualSql(sqlBuilder.toString(), sqlParameterSource);
        Object[] actualArgs = getActualArgs(sqlParameterSource);
        for (Class<?> listenerClass : query.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        String countSql = countSqlBuilder.toString();
        String sql = sqlBuilder.toString();
        PageReader<?> pageReader = null;
        Object result = null;
        Exception thrown = null;
        try {
            if (StringUtils.isNotBlank(countSql)) {
                if (query.singleColumn()) {
                    pageReader = getNamedParameterJdbcTemplate().pageQuery(countSql, sql,
                            sqlParameterSource, elementType);
                } else {
                    if (Map.class.isAssignableFrom(elementType)) {
                        pageReader = getNamedParameterJdbcTemplate().pageQuery(countSql, sql,
                                sqlParameterSource);
                    } else {
                        pageReader = getNamedParameterJdbcTemplate().pageQuery(countSql, sql,
                                sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
                    }
                }
            } else {
                if (query.singleColumn()) {
                    pageReader = getNamedParameterJdbcTemplate().pageQuery(sql, sqlParameterSource,
                            elementType);
                } else {
                    if (Map.class.isAssignableFrom(elementType)) {
                        pageReader =
                                getNamedParameterJdbcTemplate().pageQuery(sql, sqlParameterSource);
                    } else {
                        pageReader = getNamedParameterJdbcTemplate().pageQuery(sql,
                                sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
                    }
                }
            }
            if (pageReader != null) {
                if (PageVo.class.isAssignableFrom(method.getReturnType())) {
                    int pageNumber = (Integer) sqlParameterSource.getValue("page");
                    int pageSize = (Integer) sqlParameterSource.getValue("pageSize");
                    PageResponse<?> pageResponse =
                            pageReader.list(PageRequest.of(pageNumber, pageSize));
                    return (result = PageVo.wrap(pageResponse));
                }
            }
            return pageReader;
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : query.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, 0, result, thrown);
        }
    }

    private Object doGet(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            throw new IllegalArgumentException("Return type must not be void or Void");
        }
        Get getter = method.getAnnotation(Get.class);
        String sql = getter.value();
        StringBuilder sqlBuilder = new StringBuilder(sql);
        SqlParameterSource sqlParameterSource =
                getSqlParameterSource(method, args, sqlBuilder, null);
        String actualSql = getActualSql(sql, sqlParameterSource);
        Object[] actualArgs = getActualArgs(sqlParameterSource);
        for (Class<?> listenerClass : getter.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        sql = sqlBuilder.toString();
        Object result = null;
        Exception thrown = null;
        try {
            if (getter.javaType()) {
                return result = getNamedParameterJdbcTemplate().queryForObject(sql,
                        sqlParameterSource, returnType);
            } else {
                if (Map.class.isAssignableFrom(returnType)) {
                    return result = getNamedParameterJdbcTemplate().queryForObject(sql,
                            sqlParameterSource, new NamedColumnMapRowMapper());
                } else {
                    return result = getNamedParameterJdbcTemplate().queryForObject(sql,
                            sqlParameterSource, new BeanPropertyRowMapper<>(returnType));
                }
            }
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : getter.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, 0, result, thrown);
        }
    }

    private Object doBatch(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            throw new IllegalArgumentException("Return type must not be void or Void");
        }
        Batch batch = method.getAnnotation(Batch.class);
        StringBuilder sqlBuilder = new StringBuilder(batch.value());
        SqlParameterSource[] sqlParameterSources =
                getSqlParameterSources(method, args, sqlBuilder, null);

        String actualSql = getActualSql(sqlBuilder.toString(), sqlParameterSources[0]);
        Object[] actualArgs = getActualArgs(sqlParameterSources[0]);
        for (Class<?> listenerClass : batch.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        String sql = sqlBuilder.toString();
        int affectedRows = 0;
        Exception thrown = null;
        try {
            int[] affects = getNamedParameterJdbcTemplate().batchUpdate(sql, sqlParameterSources);
            affectedRows = affects.length > 0 ? Arrays.stream(affects).sum() : 0;
            try {
                return returnType.cast(affectedRows);
            } catch (RuntimeException e) {
                return ConvertUtils.convert(affectedRows, returnType);
            }
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : batch.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, affectedRows, null, thrown);
        }
    }

    private Object doInsert(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            throw new IllegalArgumentException("Return type must not be void or Void");
        }
        Insert insert = method.getAnnotation(Insert.class);
        StringBuilder sqlBuilder = new StringBuilder(insert.value());
        SqlParameterSource sqlParameterSource =
                getSqlParameterSource(method, args, sqlBuilder, null);

        String actualSql = getActualSql(sqlBuilder.toString(), sqlParameterSource);
        Object[] actualArgs = getActualArgs(sqlParameterSource);
        for (Class<?> listenerClass : insert.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        String sql = sqlBuilder.toString();
        int affectedRow = 0;
        Object result = null;
        Exception thrown = null;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            affectedRow =
                    getNamedParameterJdbcTemplate().update(sql, sqlParameterSource, keyHolder);
            if (affectedRow == 0) {
                throw new InvalidDataAccessResourceUsageException(
                        "Failed to insert a new record by sql: " + sql);
            }
            Map<String, Object> keys = keyHolder.getKeys();
            if (MapUtils.isEmpty(keys)) {
                throw new NoGeneratedKeyException(sql);
            }
            result = IteratorUtils.first(keys.values().iterator());
            try {
                return returnType.cast(result);
            } catch (RuntimeException e) {
                return ConvertUtils.convert(result, returnType);
            }
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : insert.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, affectedRow, result, thrown);
        }
    }

    private Object doUpdate(Method method, Object[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            throw new IllegalArgumentException("Return type must not be void or Void");
        }
        Update update = method.getAnnotation(Update.class);
        String sql = update.value();
        StringBuilder sqlBuilder = new StringBuilder(sql);
        SqlParameterSource sqlParameterSource =
                getSqlParameterSource(method, args, sqlBuilder, null);

        String actualSql = getActualSql(sql, sqlParameterSource);
        Object[] actualArgs = getActualArgs(sqlParameterSource);
        for (Class<?> listenerClass : update.listeners()) {
            DaoListener daoListener =
                    (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
            daoListener.beforeExecution(method, startTime, actualSql, actualArgs, this);
        }
        sql = sqlBuilder.toString();
        int affectedRows = 0;
        Exception thrown = null;
        try {
            affectedRows = getNamedParameterJdbcTemplate().update(sql, sqlParameterSource);
            try {
                return returnType.cast(affectedRows);
            } catch (RuntimeException e) {
                return ConvertUtils.convert(affectedRows, returnType);
            }
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            for (Class<?> listenerClass : update.listeners()) {
                DaoListener daoListener =
                        (DaoListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
                daoListener.afterExecution(method, startTime, actualSql, actualArgs, thrown, this);
            }
            showLog(method, startTime, actualSql, actualArgs, affectedRows, null, thrown);
        }
    }

    private String getActualSql(String sql, SqlParameterSource sqlParameterSource) {
        return NamedParameterUtils.substituteNamedParameters(sql, sqlParameterSource);
    }

    private Object[] getActualArgs(SqlParameterSource sqlParameterSource) {
        return sqlParameterSource != null && sqlParameterSource.getParameterNames() != null
                ? Arrays.stream(sqlParameterSource.getParameterNames())
                        .map(n -> sqlParameterSource.getValue(n)).toArray()
                : new Object[0];
    }

    private void showLog(Method method, long startTime, String sql, Object[] args, int affectedRows,
            Object result, Exception thrown) {
        if (log.isTraceEnabled() || thrown != null) {
            final String methodKey =
                    String.format("[%s#%s] ", interfaceClass.getSimpleName(), method.getName());
            List<Object> logParams =
                    new ArrayList<>(Arrays.asList(sql, System.currentTimeMillis() - startTime,
                            ArrayUtils.isNotEmpty(args) ? Arrays.deepToString(args) : "[]"));
            List<String> logs = new ArrayList<>();
            logs.add("Execute Sql: {}");
            logs.add("Take: {} ms");
            logs.add("Args: {}");
            if (affectedRows > 0) {
                logs.add("Affected Rows: {}");
                logParams.add(affectedRows);
            }
            if (result != null) {
                if (result instanceof Object[]) {
                    logs.add("Multiline Result: ");
                    for (Object o : (Object[]) result) {
                        logs.add("  " + o.toString());
                    }
                } else if (result instanceof Collection) {
                    logs.add("Multiline Result: ");
                    for (Object o : (Collection) result) {
                        logs.add("  " + o.toString());
                    }
                } else if (result instanceof PageVo) {
                    PageVo pageVo = (PageVo<?>) result;
                    List<?> list = pageVo.getContent();
                    logs.add("Multiline Result: " + pageVo.toString());
                    for (Object o : list) {
                        logs.add("  " + o.toString());
                    }
                } else {
                    logs.add("Single Result: " + result.toString());
                }
            }
            if (thrown != null) {
                LogUtils.error(methodKey, logs, log, null, logParams.toArray());
            } else {
                LogUtils.trace(methodKey, logs, log, null, logParams.toArray());
            }

        }
    }

    private SqlParameterSource getSqlParameterSource(Method method, Object[] args,
            StringBuilder sqlBuilder, StringBuilder countSqlBuilder) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        Parameter[] methodParameters = method.getParameters();
        Parameter methodParameter;
        Annotation[] annotations;
        Annotation annotation;
        for (int i = 0; i < methodParameters.length; i++) {
            methodParameter = methodParameters[i];
            annotations = methodParameter.getAnnotations();
            if (ArrayUtils.isEmpty(annotations)) {
                continue;
            }
            annotation = annotations[0];
            if (annotation instanceof Arg) {
                if (((Arg) annotation).skip()) {
                    continue;
                }
                String key = ((Arg) annotation).value();
                if (StringUtils.isBlank(key)) {
                    key = methodParameter.getName();
                }
                parameters.put(key, args[i]);
            } else if (annotation instanceof Example) {
                if (args[i] instanceof Map) {
                    parameters.putAll((Map<String, Object>) args[i]);
                } else {
                    parameters.putAll(BeanMapUtils.beanToMap(args[i], null, true, null));
                }
                String[] excludedProperties = ((Example) annotation).excludedProperties();
                if (ArrayUtils.isNotEmpty(excludedProperties)) {
                    MapUtils.removeKeys(parameters, excludedProperties);
                }
            } else if (annotation instanceof Sql) {
                if (args[i] instanceof CharSequence) {
                    String sql = sqlBuilder.toString();
                    int length = sql.length();
                    sql = sql.replaceFirst("@sql", args[i].toString());
                    sqlBuilder.delete(0, length);
                    sqlBuilder.append(sql);

                    if (StringUtils.isNotBlank(countSqlBuilder)) {
                        sql = countSqlBuilder.toString();
                        length = sql.length();
                        sql = sql.replaceFirst("@sql", args[i].toString());
                        countSqlBuilder.delete(0, length);
                        countSqlBuilder.append(sql);
                    }
                }
            }
        }
        return new InternalSqlParameterSource(new MapSqlParameterSource(parameters), typeHandlers);
    }

    private SqlParameterSource[] getSqlParameterSources(Method method, Object[] args,
            StringBuilder sqlBuilder, StringBuilder countSqlBuilder) {
        List<SqlParameterSource> sqlParameterList = new ArrayList<SqlParameterSource>();
        Parameter[] methodParameters = method.getParameters();
        Parameter methodParameter;
        Annotation[] annotations;
        Annotation annotation;
        for (int i = 0; i < methodParameters.length; i++) {
            methodParameter = methodParameters[i];
            annotations = methodParameter.getAnnotations();
            if (ArrayUtils.isEmpty(annotations)) {
                continue;
            }
            annotation = annotations[0];
            if (annotation instanceof Args) {
                if (args[i] instanceof Object[]) {
                    for (Object object : (Object[]) args[i]) {
                        sqlParameterList.add(getSqlParameterSource(object, null));
                    }
                } else if (args[i] instanceof Collection) {
                    for (Object object : (Collection) args[i]) {
                        sqlParameterList.add(getSqlParameterSource(object, null));
                    }
                }
            } else if (annotation instanceof Example) {
                SqlParameterSource sqlParameterSource =
                        getSqlParameterSource(args[i], ((Example) annotation).excludedProperties());
                sqlParameterList.add(sqlParameterSource);
            } else if (annotation instanceof Sql) {
                if (args[i] instanceof CharSequence) {
                    String sql = sqlBuilder.toString();
                    int length = sql.length();
                    sql = sql.replaceFirst("@sql", args[i].toString());
                    sqlBuilder.delete(0, length);
                    sqlBuilder.append(sql);

                    if (countSqlBuilder != null && StringUtils.isNotBlank(countSqlBuilder)) {
                        sql = countSqlBuilder.toString();
                        length = sql.length();
                        sql = sql.replaceFirst("@sql", args[i].toString());
                        countSqlBuilder.delete(0, length);
                        countSqlBuilder.append(sql);
                    }
                }
            }
        }
        return sqlParameterList.toArray(new SqlParameterSource[0]);
    }

    private SqlParameterSource getSqlParameterSource(Object object, String[] excludedProperties) {
        Map<String, Object> parameters;
        if (object instanceof Map) {
            parameters = (Map<String, Object>) object;
        } else {
            parameters = BeanMapUtils.beanToMap(object, null, true, null);
        }
        if (ArrayUtils.isNotEmpty(excludedProperties)) {
            MapUtils.removeKeys(parameters, excludedProperties);
        }
        return new InternalSqlParameterSource(new MapSqlParameterSource(parameters), typeHandlers);
    }

    public List<TypeHandler> getTypeHandlers() {
        return typeHandlers;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public String toString() {
        return interfaceClass.getName() + "$ProxyByJDK";
    }

    /**
     * 
     * @Description: InternalSqlParameterSource
     * @Author: Fred Feng
     * @Date: 14/01/2025
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private static class InternalSqlParameterSource implements SqlParameterSource {

        private final SqlParameterSource sqlParameterSource;
        private final List<TypeHandler> typeHandlers;

        @Override
        public boolean hasValue(String paramName) {
            return sqlParameterSource.hasValue(paramName);
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            Object originalValue = sqlParameterSource.getValue(paramName);
            if (originalValue == null) {
                return null;
            }
            Optional<TypeHandler> opt = Optional.ofNullable(typeHandlers).map(
                    handlers -> handlers.stream().filter(h -> h.support(originalValue)).findFirst())
                    .get();
            return opt.isPresent() ? opt.get().convertValue(originalValue) : originalValue;
        }

        @Override
        public int getSqlType(String paramName) {
            return sqlParameterSource.getSqlType(paramName);
        }

        @Override
        public String getTypeName(String paramName) {
            return sqlParameterSource.getTypeName(paramName);
        }

        @Override
        public String[] getParameterNames() {
            return sqlParameterSource.getParameterNames();
        }

    }

}
