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
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty.Action.EMPTY
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty.Action.NULL
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty.Action.SKIP
import io.github.airflux.dsl.writer.`object`.builder.property.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
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
            val writer = writer<DTO> {
                property(optional(name = ATTRIBUTE_NAME, from = DTO::value, writer = DummyWriter { JsString(it) }))
            }

            "when have some attributes for writing to an object" - {
                val value = DTO(value = ATTRIBUTE_VALUE)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should returns the object with some attributes" {
                    result as JsObject
                    result shouldBe JsObject(ATTRIBUTE_NAME to JsString(ATTRIBUTE_VALUE))
                }
            }

            "when no attributes for writing to an object" - {
                val value = DTO(value = null)

                "if valueIfObjectIsEmpty equals EMPTY" - {
                    val result = writer.write(
                        context = CONTEXT + WriteActionIfObjectIsEmpty(value = EMPTY),
                        location = LOCATION,
                        value = value
                    )

                    "then should returns the empty JsObject" {
                        result as JsObject
                        result shouldBe JsObject()
                    }
                }

                "if valueIfObjectIsEmpty equals NULL" - {
                    val result = writer.write(
                        context = CONTEXT + WriteActionIfObjectIsEmpty(value = NULL),
                        location = LOCATION,
                        value = value
                    )

                    "then should returns the JsNull value" {
                        result shouldBe JsNull
                    }
                }

                "if valueIfObjectIsEmpty equals SKIP" - {
                    val result = writer.write(
                        context = CONTEXT + WriteActionIfObjectIsEmpty(value = SKIP),
                        location = LOCATION,
                        value = value
                    )

                    "then should returns the null value" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }

    internal data class DTO(val value: String?)
}
