/*
 * Copyright 2021-2022 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.core.path.JsPath

public class JsObjectProperties(private val items: List<JsObjectProperty>) : Collection<JsObjectProperty> by items {

    internal class Builder {
        private val properties = mutableListOf<JsObjectProperty>()

        fun add(property: JsObjectProperty) {
            properties.add(property)
        }

        internal fun build(checkUniquePropertyPath: Boolean): JsObjectProperties {
            if (checkUniquePropertyPath) checkingUniquePropertyPath(properties)
            return JsObjectProperties(properties)
        }

        internal fun checkingUniquePropertyPath(properties: List<JsObjectProperty>) {
            val paths = mutableSetOf<JsPath>()
            properties.forEach { property ->
                property.path.items.forEach { path ->
                    if (path in paths)
                        error("The path $path is already.")
                }
            }
        }
    }
}