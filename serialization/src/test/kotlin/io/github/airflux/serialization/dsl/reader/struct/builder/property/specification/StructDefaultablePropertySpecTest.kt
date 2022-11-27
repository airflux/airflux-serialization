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

package io.github.airflux.serialization.dsl.reader.struct.builder.property.specification

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyIntReader
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.validator.condition.applyIfNotNull
import io.github.airflux.serialization.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.serialization.std.validator.string.StdStringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class StructDefaultablePropertySpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"
        private const val DEFAULT_VALUE = "none"

        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private val StringReader = dummyStringReader<EB, Unit>()
        private val IntReader = dummyIntReader<EB, Unit>()
        private val DEFAULT = { _: ReaderEnv<EB, Unit> -> DEFAULT_VALUE }
    }

    init {

        "The StructPropertySpec#Defaultable type" - {

            "when creating the instance by a property name" - {
                val spec = defaultable(name = "id", reader = StringReader, default = DEFAULT)

                "then the paths parameter must contain only the passed path" {
                    spec.path.items shouldContainExactly listOf(PropertyPath("id"))
                }

                "when the reader has read a property named id" - {

                    "if the property value is not the null type" - {
                        val source = StructNode("id" to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }

                    "if the property value is the null type" - {
                        val source = StructNode("id" to NullNode)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the default value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe DEFAULT_VALUE
                        }
                    }
                }

                "when the property does not founded" - {
                    val source = StructNode("code" to StringNode(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then a default value should be returned" {
                        result as ReaderResult.Success<String>
                        result.value shouldBe DEFAULT_VALUE
                    }
                }

                "when a read error occurred" - {
                    val source = StructNode("id" to NumberNode.valueOf(10))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then should be returned a read error" {
                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when creating the instance by a single-path" - {
                val path = PropertyPath("id")
                val spec = defaultable(path = path, reader = StringReader, default = DEFAULT)

                "then the paths parameter must contain only the passed path" {
                    spec.path.items shouldContainExactly listOf(path)
                }

                "when the reader has read a property named id" - {

                    "if the property value is not the null type" - {
                        val source = StructNode("id" to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }

                    "if the property value is the null type" - {
                        val source = StructNode("id" to NullNode)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the default value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe DEFAULT_VALUE
                        }
                    }
                }

                "when the property does not founded" - {
                    val source = StructNode("code" to StringNode(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then a default value should be returned" {
                        result as ReaderResult.Success<String>
                        result.value shouldBe DEFAULT_VALUE
                    }
                }

                "when an error occurs while reading" - {
                    val source = StructNode("id" to NumberNode.valueOf(10))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then should be returned a read error" {
                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when creating the instance by a multi-path" - {
                val idPath = PropertyPath("id")
                val identifierPath = PropertyPath("identifier")
                val spec = defaultable(
                    paths = PropertyPaths(idPath, identifierPath),
                    reader = StringReader,
                    default = DEFAULT
                )

                "then the paths parameter must contain only the passed paths" {
                    spec.path.items shouldContainExactly listOf(idPath, identifierPath)
                }

                "when the reader has read a property named id" - {

                    "if the property value is not the null type" - {
                        val source = StructNode("id" to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }

                    "if the property value is the null type" - {
                        val source = StructNode("id" to NullNode)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the default value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe DEFAULT_VALUE
                        }
                    }
                }

                "when the reader has read a property named identifier" - {

                    "if the property value is not the null type" - {
                        val source = StructNode("identifier" to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }

                    "if the property value is the null type" - {
                        val source = StructNode("identifier" to NullNode)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the default value should be returned" {
                            result as ReaderResult.Success<String>
                            result.value shouldBe DEFAULT_VALUE
                        }
                    }
                }

                "when the property does not founded" - {
                    val source = StructNode("code" to StringNode(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then a default value should be returned" {
                        result as ReaderResult.Success<String>
                        result.value shouldBe DEFAULT_VALUE
                    }
                }

                "when an error occurs while reading" - {
                    val source = StructNode("id" to NumberNode.valueOf(10))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then should be returned a read error" {
                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec =
                    StructPropertySpec.Defaultable(path = PropertyPaths(PropertyPath("id")), reader = StringReader)
                val specWithValidator = spec.validation(StdStringValidator.isNotEmpty<EB, Unit>().applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = StringNode(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = StringNode("")

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val source = NumberNode.valueOf(10)

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = defaultable(name = "id", reader = StringReader, default = DEFAULT)
                val alt = defaultable(
                    name = "id",
                    reader = IntReader.map { it.toString() },
                    default = DEFAULT
                )
                val specWithAlternative = spec or alt

                "then the paths parameter must contain all elements from both spec" {
                    specWithAlternative.path.items shouldContainExactly listOf(PropertyPath("id"))
                }

                "when the main reader has successfully read" - {
                    val source = StructNode("id" to StringNode(ID_VALUE_AS_UUID))
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned" {
                        result as ReaderResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }
                }

                "when the main reader has failure read" - {
                    val source = StructNode("id" to NumberNode.valueOf(ID_VALUE_AS_INT)!!)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result as ReaderResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_INT
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = StructNode("id" to BooleanNode.True)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then should be returned all read errors" {
                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder,
                        IsNotEmptyStringValidator.ErrorBuilder {

        override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

        override fun isNotEmptyStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsEmpty
    }
}
