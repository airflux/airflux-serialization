/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsPathReader
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.kotest.assertions.cause
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec

internal class PropertiesStructReaderTest : FreeSpec() {

    init {

        "The PropertiesStructReader type" - {

            "when the reader was created" - {
                val reader = PropertiesStructReader(
                    properties = properties,
                    typeBuilder = { _, _ ->
                        DTO(id = +idProperty, name = +nameProperty).toSuccess(LOCATION)
                    }
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "when some property reader returns an error" - {
                        val source = JsStruct(
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }

                    "when multiple property readers return an error" - {
                        val source = JsStruct()

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                    "when some property reader returns an error" - {
                        val source = JsStruct(
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }

                    "when multiple property readers return an error" - {
                        val source = JsStruct()

                        "then the reader should return all errors" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                ),
                                cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }
            }

            "when the result builder throw some exception" - {
                val reader = PropertiesStructReader<EB, OPTS, DTO>(
                    properties = properties,
                    typeBuilder = { _, _ ->
                        throw InternalException()
                    }
                )

                val source = JsStruct(
                    ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<InternalException> {
                            reader.read(envWithFailFastIsTrue, LOCATION, source)
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<InternalException> {
                            reader.read(envWithFailFastIsFalse, LOCATION, source)
                        }
                    }
                }
            }
        }
    }

    companion object {

        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "42"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"

        private val ENV_WITH_FAIL_FAST_IS_TRUE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))
        private val ENV_WITH_FAIL_FAST_IS_FALSE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()

        private val idProperty = property(ID_PROPERTY_NAME, StringReader)
        private val nameProperty = property(NAME_PROPERTY_NAME, StringReader)
        private val properties = listOf(idProperty, nameProperty)

        private fun <EB, O, P> property(name: String, reader: JsReader<EB, O, P>): StructProperty<EB, O, P>
            where EB : InvalidTypeErrorBuilder,
                  EB : PathMissingErrorBuilder {
            val path = JsPath(name)
            val spec = StructPropertySpec(paths = JsPaths(path), reader = JsPathReader.required(path, reader))
            return StructProperty(spec)
        }
    }

    private class InternalException : RuntimeException()

    private data class DTO(val id: String, val name: String?)

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
