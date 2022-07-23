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

package io.github.airflux.dsl.writer.`object`.builder

import io.github.airflux.common.DummyWriter
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.dsl.writer.`object`.builder.property.specification.nonNullable
import io.github.airflux.dsl.writer.`object`.builder.property.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsObjectWriterBuilderTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "name"
        private const val ATTRIBUTE_VALUE = "user"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsObjectWriterBuilder type" - {

            "when have some attributes for writing to an object" - {
                val from: (String) -> String = { it }
                val writer = writer {
                    property(nonNullable(name = ATTRIBUTE_NAME, from = from, writer = DummyWriter { JsString(it) }))
                }

                "then should returns the object with some attributes" {
                    val result = writer.write(context = CONTEXT, location = LOCATION, value = ATTRIBUTE_VALUE)
                    result shouldBe JsObject(ATTRIBUTE_NAME to JsString(ATTRIBUTE_VALUE))
                }
            }

            "when no attributes for writing to an object" - {
                val from: (String) -> String? = { null }
                val writer = writer {
                    property(optional(name = ATTRIBUTE_NAME, from = from, writer = DummyWriter { JsString(it) }))
                }

                "then should returns the empty JsObject" {
                    val result = writer.write(context = CONTEXT, location = LOCATION, value = ATTRIBUTE_VALUE)
                    result shouldBe JsObject()
                }
            }
        }
    }
}
