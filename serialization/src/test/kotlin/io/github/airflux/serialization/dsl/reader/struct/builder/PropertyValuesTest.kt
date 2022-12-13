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

package io.github.airflux.serialization.dsl.reader.struct.builder

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.defaultable
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.nullable
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.nullableWithDefault
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optionalWithDefault
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.std.validator.string.IsNotEmptyStringValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class PropertyValuesTest : FreeSpec() {

    companion object {
        private const val UNKNOWN_PROPERTY_NAME = "name"
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "dd7c5dab-0f8b-4436-a8e8-72b841c90acc"
        private const val DEFAULT_VALUE = "None"
        private val DEFAULT = { _: ReaderEnv<EB, Unit> -> DEFAULT_VALUE }
        private val StringReader = dummyStringReader<EB, Unit>()
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

                "for required property" - {
                    val property = StructProperty.Required(required(PROPERTY_NAME, StringReader))

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

                "for defaultable property" - {
                    val property =
                        StructProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, DEFAULT))

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

                "for optional property" - {
                    val property = StructProperty.Optional(optional(PROPERTY_NAME, StringReader))

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

                "for optional with default property" - {
                    val property = StructProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )

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
                    val property = StructProperty.Nullable(nullable(PROPERTY_NAME, StringReader))

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

                "for nullable with default property" - {
                    val property = StructProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )

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

                "for required property" - {
                    val property = StructProperty.Required(required(PROPERTY_NAME, StringReader))
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
                        val unknownProperty = StructProperty.Required(required(UNKNOWN_PROPERTY_NAME, StringReader))

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

                "for defaultable property" - {
                    val property =
                        StructProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, DEFAULT))
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
                        val unknownProperty = StructProperty.Defaultable(
                            defaultable(UNKNOWN_PROPERTY_NAME, StringReader, DEFAULT)
                        )

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

                "for optional property" - {
                    val property = StructProperty.Optional(optional(PROPERTY_NAME, StringReader))
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
                        val unknownProperty = StructProperty.Optional(optional(UNKNOWN_PROPERTY_NAME, StringReader))

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

                "for optional with default property" - {
                    val property = StructProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )
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
                        val unknownProperty = StructProperty.OptionalWithDefault(
                            optionalWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, DEFAULT)
                        )

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
                    val property = StructProperty.Nullable(nullable(PROPERTY_NAME, StringReader))
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
                        val unknownProperty = StructProperty.Nullable(nullable(UNKNOWN_PROPERTY_NAME, StringReader))

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

                "for nullable with default property" - {
                    val property = StructProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )
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
                        val unknownProperty = StructProperty.NullableWithDefault(
                            nullableWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, DEFAULT)
                        )

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

                "for required property" - {
                    val property = StructProperty.Required(required(PROPERTY_NAME, StringReader))
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<NoSuchElementException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<NoSuchElementException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for defaultable property" - {
                    val property =
                        StructProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, DEFAULT))
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<NoSuchElementException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<NoSuchElementException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for optional property" - {
                    val property = StructProperty.Optional(optional(PROPERTY_NAME, StringReader))
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
                        val unknownProperty = StructProperty.Optional(optional(UNKNOWN_PROPERTY_NAME, StringReader))

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

                "for optional with default property" - {
                    val property = StructProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<NoSuchElementException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<NoSuchElementException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for nullable property" - {
                    val property = StructProperty.Nullable(nullable(PROPERTY_NAME, StringReader))
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
                        val unknownProperty = StructProperty.Nullable(nullable(UNKNOWN_PROPERTY_NAME, StringReader))

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

                "for nullable with default property" - {
                    val property = StructProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, DEFAULT)
                    )
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
                        val unknownProperty = StructProperty.NullableWithDefault(
                            nullableWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, DEFAULT)
                        )

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
                        PathMissingErrorBuilder,
                        IsNotEmptyStringValidator.ErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

        override fun isNotEmptyStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsEmpty
    }
}
