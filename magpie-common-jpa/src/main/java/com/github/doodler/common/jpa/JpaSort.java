/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

/**
 * 
 * JpaSort
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface JpaSort {

	Order toOrder(Model<?> model, CriteriaBuilder builder);

	static JpaSort asc(String attributeName) {
		return asc(Property.forName(attributeName));
	}

	static JpaSort asc(String name, String attributeName) {
		return asc(Property.forName(name, attributeName));
	}

	static JpaSort asc(Field<?> field) {
		return new JpaSort() {
			public Order toOrder(Model<?> model, CriteriaBuilder builder) {
				return builder.asc(field.toExpression(model, builder));
			}
		};
	}

	static JpaSort desc(String attributeName) {
		return desc(Property.forName(attributeName));
	}

	static JpaSort desc(String name, String attributeName) {
		return desc(Property.forName(name, attributeName));
	}

	static JpaSort desc(Field<?> field) {
		return new JpaSort() {
			public Order toOrder(Model<?> model, CriteriaBuilder builder) {
				return builder.desc(field.toExpression(model, builder));
			}
		};
	}

}
