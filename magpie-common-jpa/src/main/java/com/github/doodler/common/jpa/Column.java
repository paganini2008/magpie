/**
 * Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)
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
package com.github.doodler.common.jpa;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Selection;
import com.github.doodler.common.jpa.LambdaUtils.LambdaInfo;

/**
 * 
 * Column
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface Column {

    Selection<?> toSelection(Model<?> model, CriteriaBuilder builder);

    static Column forName(String attributeName) {
        return forName(null, attributeName);
    }

    static Column forName(String alias, String attributeName) {
        return forName(alias, attributeName, null);
    }

    static Column forName(String attributeName, Class<?> requiredType) {
        return forName(null, attributeName, requiredType);
    }

    static Column forName(String alias, String attributeName, Class<?> requiredType) {
        return Property.forName(alias, attributeName, requiredType).as(attributeName);
    }

    static <X, T> Column forName(SerializedFunction<X, ?> sf, Class<T> requiredType) {
        LambdaInfo info = LambdaUtils.inspect(sf);
        String alias = TableAlias.get(info.getClassName());
        return new Property<T>(alias, info.getAttributeName(), requiredType).as(info.getAttributeName());
    }

    static Column forSubQuery(SubQueryBuilder<?> subQueryBuilder) {
        return new Column() {

            @Override
            public Selection<?> toSelection(Model<?> model, CriteriaBuilder builder) {
                return subQueryBuilder.toSubquery(builder).getSelection();
            }
        };
    }

    static Column construct(Class<?> resultClass, String alias, String[] attributeNames) {
        return new Column() {

            @Override
            public Selection<?> toSelection(Model<?> model, CriteriaBuilder builder) {
                List<Selection<?>> selections = model.getSelections(alias, attributeNames);
                return builder.construct(resultClass, selections.toArray(new Selection[0]));
            }
        };
    }

}
