package com.github.doodler.common.context;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: BeanLifeCycleUtils
 * @Author: Fred Feng
 * @Date: 12/01/2025
 * @Version 1.0.0
 */
@Slf4j
@UtilityClass
public class BeanLifeCycleUtils {

    public void afterPropertiesSet(Object bean) throws Exception {
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }

    public void afterPropertiesSet(Iterable<?> beans) throws Exception {
        for (Object bean : beans) {
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
        }
    }

    public void destroy(Object bean) throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }
    }

    public void destroy(Iterable<?> beans) throws Exception {
        for (Object bean : beans) {
            if (bean instanceof DisposableBean) {
                ((DisposableBean) bean).destroy();
            }
        }
    }

    public void destroyQuietly(Object bean) {
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void destroyQuietly(Iterable<?> beans) {
        for (Object bean : beans) {
            if (bean instanceof DisposableBean) {
                try {
                    ((DisposableBean) bean).destroy();
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
