package com.github.doodler.common.jpa;

/**
 * 
 * @Description: LogicalFilter
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public abstract class LogicalFilter implements Filter {

    public LogicalFilter and(Filter filter) {
        return new AndFilter(this, filter);
    }

    public LogicalFilter or(Filter filter) {
        return new OrFilter(this, filter);
    }

    public LogicalFilter not() {
        return new NotFilter(this);
    }

}
