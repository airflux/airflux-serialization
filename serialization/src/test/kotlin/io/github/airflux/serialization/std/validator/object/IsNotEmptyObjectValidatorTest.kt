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

package io.github.airflux.serialization.std.validator.`object`

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsNotEmptyObjectValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private val LOCATION = JsLocation.empty
        private val PROPERTIES: ObjectProperties = ObjectProperties(emptyList())
    }

    init {

        "The object validator IsNotEmpty" - {
            val validator: ObjectValidator = StdObjectValidator.isNotEmpty.build(PROPERTIES)

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()
                val input = StructNode()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, PROPERTIES, input)
                    }
                    exception.message shouldBe "The error builder '${IsNotEmptyObjectValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.IsEmpty }
                )

                "when the object is empty" - {
                    val input = StructNode()

                    "then the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, PROPERTIES, input)
                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Object.IsEmpty
                        )
                    }
                }

                "when the object is not empty" - {
                    val input = StructNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, PROPERTIES, input)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
