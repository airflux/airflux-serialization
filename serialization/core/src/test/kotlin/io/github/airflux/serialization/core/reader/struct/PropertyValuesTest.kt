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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.struct.property.JsStructProperty
import io.github.airflux.serialization.core.reader.struct.property.PropertyValues
import io.github.airflux.serialization.core.reader.struct.property.PropertyValuesInstance
import io.github.airflux.serialization.core.reader.struct.property.specification.optional
import io.github.airflux.serialization.core.reader.struct.property.specification.required
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class PropertyValuesTest : FreeSpec() {

    companion object {
        private const val UNKNOWN_PROPERTY_NAME = "name"
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "dd7c5dab-0f8b-4436-a8e8-72b841c90acc"
        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
    }

    init {

        "The PropertyValues type" - {

            "when not added any value" - {
                val map: PropertyValues<EB, Unit> = PropertyValuesInstance()

                "then the property isEmpty should return true" {
                    map.isEmpty shouldBe true
                }

                "then the property isNotEmpty should return false" {
                    map.isNotEmpty shouldBe false
                }

                "then the property size should return 0" {
                    map.size shouldBe 0
                }

                "for non-nullable property" - {
                    val property = JsStructProperty(required(PROPERTY_NAME, StringReader))

                    "then the method 'get' should throw an exception" {
                        shouldThrow<NoSuchElementException> { map[property] }
                    }

                    "then the method 'unaryPlus' should throw an exception" {
                        shouldThrow<NoSuchElementException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for nullable property" - {
                    val property = JsStructProperty(optional(PROPERTY_NAME, StringReader))

                    "then the method 'get' should throw an exception" {
                        shouldThrow<NoSuchElementException> { map[property] }
                    }

                    "then the method 'unaryPlus' should throw an exception" {
                        shouldThrow<NoSuchElementException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }
            }

            "when the added value is not null" - {

                "for non-nullable property" - {
                    val property = JsStructProperty(required(PROPERTY_NAME, StringReader))
                    val map: PropertyValues<EB, Unit> = PropertyValuesInstance<EB, Unit>().apply {
                        this[property] = PROPERTY_VALUE
                    }

                    "then the property isEmpty should return false" {
                        map.isEmpty shouldBe false
                    }

                    "then the property isNotEmpty should return true" {
                        map.isNotEmpty shouldBe true
                    }

                    "then the property size should return 1" {
                        map.size shouldBe 1
                    }

                    "then for known property" - {

                        "the method 'get' should return the appended value" {
                            val value = map[property]
                            value shouldBe PROPERTY_VALUE
                        }

                        "the method 'unaryPlus' should return the appended value" {
                            with(map) {
                                val value = +property
                                value shouldBe PROPERTY_VALUE
                            }
                        }
                    }

                    "then for unknown property" - {
                        val unknownProperty = JsStructProperty(required(UNKNOWN_PROPERTY_NAME, StringReader))

                        "the method 'get' should thrown an exception" {
                            shouldThrow<NoSuchElementException> { map[unknownProperty] }
                        }

                        "the method 'unaryPlus' should thrown an exception" {
                            shouldThrow<NoSuchElementException> {
                                with(map) {
                                    +unknownProperty
                                }
                            }
                        }
                    }
                }

                "for nullable property" - {
                    val property = JsStructProperty(optional(PROPERTY_NAME, StringReader))
                    val map: PropertyValues<EB, Unit> = PropertyValuesInstance<EB, Unit>().apply {
                        this[property] = PROPERTY_VALUE
                    }

                    "then the property isEmpty should return false" {
                        map.isEmpty shouldBe false
                    }

                    "then the property isNotEmpty should return true" {
                        map.isNotEmpty shouldBe true
                    }

                    "then the property size should return 1" {
                        map.size shouldBe 1
                    }

                    "then for known property" - {

                        "the method 'get' should return the appended value" {
                            val value = map[property]
                            value shouldBe PROPERTY_VALUE
                        }

                        "the method 'unaryPlus' should return the appended value" {
                            with(map) {
                                val value = +property
                                value shouldBe PROPERTY_VALUE
                            }
                        }
                    }

                    "then for unknown property" - {
                        val unknownProperty = JsStructProperty(optional(UNKNOWN_PROPERTY_NAME, StringReader))

                        "the method 'get' should thrown an exception" {
                            shouldThrow<NoSuchElementException> { map[unknownProperty] }
                        }

                        "the method 'unaryPlus' should thrown an exception" {
                            shouldThrow<NoSuchElementException> {
                                with(map) {
                                    +unknownProperty
                                }
                            }
                        }
                    }
                }
            }

            "when the added value is null" - {

                "for nullable property" - {
                    val property = JsStructProperty(optional(PROPERTY_NAME, StringReader))
                    val map: PropertyValues<EB, Unit> = PropertyValuesInstance<EB, Unit>().apply {
                        this[property] = null
                    }

                    "then the property isEmpty should return true" {
                        map.isEmpty shouldBe true
                    }

                    "then the property isNotEmpty should return false" {
                        map.isNotEmpty shouldBe false
                    }

                    "then the property size should return 0" {
                        map.size shouldBe 0
                    }

                    "then for known property" - {

                        "the method 'get' should return the appended value" {
                            val value = map[property]
                            value.shouldBeNull()
                        }

                        "the method 'unaryPlus' should return the appended value" {
                            with(map) {
                                val value = +property
                                value.shouldBeNull()
                            }
                        }
                    }

                    "then for unknown property" - {
                        val unknownProperty = JsStructProperty(optional(UNKNOWN_PROPERTY_NAME, StringReader))

                        "the method 'get' should thrown an exception" {
                            shouldThrow<NoSuchElementException> { map[unknownProperty] }
                        }

                        "the method 'unaryPlus' should thrown an exception" {
                            shouldThrow<NoSuchElementException> {
                                with(map) {
                                    +unknownProperty
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }
}
