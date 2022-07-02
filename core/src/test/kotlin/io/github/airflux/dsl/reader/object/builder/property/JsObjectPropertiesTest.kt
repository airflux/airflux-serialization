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

package io.github.airflux.dsl.reader.`object`.builder.property

import io.github.airflux.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsObjectPropertiesTest : FreeSpec() {

    companion object {
        private val ID_PROPERTY = JsObjectProperty.Required(required(name = "id", reader = StringReader))
        private val NAME_PROPERTY = JsObjectProperty.Required(required(name = "name", reader = StringReader))
    }

    init {

        "The JsObjectProperties type" - {

            "when no property is added to the builder" - {
                val properties = JsObjectProperties.Builder().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when some property is added to the builder" - {
                val properties = JsObjectProperties.Builder()
                    .apply {
                        add(ID_PROPERTY)
                        add(NAME_PROPERTY)
                    }
                    .build()

                "should have contain only passed elements in order they were passed" {
                    properties shouldContainExactly listOf(ID_PROPERTY, NAME_PROPERTY)
                }
            }
        }
    }
}
