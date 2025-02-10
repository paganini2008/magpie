package com.github.doodler.common.mybatis.utils;

import static com.github.doodler.common.mybatis.StringPool.SQL_LIMIT_SYNTAX_FORMAT;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;

/**
 * @Description: DefaultQueryPageReader
 * @Author: Fred Feng
 * @Date: 17/03/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class DefaultQueryPageReader<T> implements PageReader<T> {

    private final IService<T> service;
    private final Wrapper<T> query;

    public DefaultQueryPageReader(IService<T> service, Wrapper<T> query) {
        this.service = service;
        this.query = query;
    }

    @Override
    public PageContent<T> list(int pageNumber, int offset, int limit, Object nextToken) {
        if (!(query instanceof Join)) {
            throw new UnsupportedOperationException(
                    "Unknown query type: " + query.getClass().getName());
        }
        Wrapper<T> ref = (Wrapper<T>) ((Join) query)
                .last(String.format(SQL_LIMIT_SYNTAX_FORMAT, limit, offset));
        List<T> list = service.list(ref);
        return new DefaultPageContent<>(list, null);
    }
}
