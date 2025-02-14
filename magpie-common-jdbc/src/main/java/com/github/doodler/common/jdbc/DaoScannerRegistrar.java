/**
 * Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.doodler.common.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import com.github.doodler.common.jdbc.annotations.DaoScan;

/**
 * 
 * @Description: DaoScannerRegistrar
 * @Author: Fred Feng
 * @Date: 31/08/2024
 * @Version 1.0.0
 */
public class DaoScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(DaoScan.class.getName()));
        List<String> basePackages = new ArrayList<String>();
        if (annotationAttributes.containsKey("basePackages")) {
            for (String basePackage : annotationAttributes.getStringArray("basePackages")) {
                if (StringUtils.hasText(basePackage)) {
                    basePackages.add(basePackage);
                }
            }
        }
        ClassPathBeanDefinitionScanner scanner = new ClassPathDaoScanner(registry);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        scanner.scan(StringUtils.toStringArray(basePackages));
    }

}
