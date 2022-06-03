package io.github.airflux.dsl.reader

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.ObjectValuesMapInstance
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.property.specification.defaultable
import io.github.airflux.dsl.reader.`object`.property.specification.nullable
import io.github.airflux.dsl.reader.`object`.property.specification.nullableWithDefault
import io.github.airflux.dsl.reader.`object`.property.specification.optional
import io.github.airflux.dsl.reader.`object`.property.specification.optionalWithDefault
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class ObjectValuesMapTest : FreeSpec() {

    companion object {
        private const val UNKNOWN_PROPERTY_NAME = "name"
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "dd7c5dab-0f8b-4436-a8e8-72b841c90acc"
        private const val PROPERTY_DEFAULT_VALUE = "None"
        private val PROPERTY_DEFAULT = { PROPERTY_DEFAULT_VALUE }
    }

    init {

        "The ObjectValuesMap" - {

            "when not added any value" - {
                val map: ObjectValuesMap = ObjectValuesMapInstance()

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
                    val property = JsObjectProperty.Required(required(PROPERTY_NAME, StringReader))

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
                        JsObjectProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT))

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
                    val property = JsObjectProperty.Optional(optional(PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
                    val property = JsObjectProperty.Nullable(nullable(PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
                    val property = JsObjectProperty.Required(required(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.Required(required(UNKNOWN_PROPERTY_NAME, StringReader))

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
                        JsObjectProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.Defaultable(
                            defaultable(UNKNOWN_PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
                    val property = JsObjectProperty.Optional(optional(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.Optional(optional(UNKNOWN_PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
                    )
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.OptionalWithDefault(
                            optionalWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
                    val property = JsObjectProperty.Nullable(nullable(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.Nullable(nullable(UNKNOWN_PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
                    )
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
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
                        val unknownProperty = JsObjectProperty.NullableWithDefault(
                            nullableWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
                    val property = JsObjectProperty.Required(required(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<IllegalStateException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<IllegalStateException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for defaultable property" - {
                    val property =
                        JsObjectProperty.Defaultable(defaultable(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<IllegalStateException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<IllegalStateException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for optional property" - {
                    val property = JsObjectProperty.Optional(optional(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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
                        val unknownProperty = JsObjectProperty.Optional(optional(UNKNOWN_PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.OptionalWithDefault(
                        optionalWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
                    )
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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

                    "the method 'get' should thrown an exception" {
                        shouldThrow<IllegalStateException> { map[property] }
                    }

                    "the method 'unaryPlus' should thrown an exception" {
                        shouldThrow<IllegalStateException> {
                            with(map) {
                                +property
                            }
                        }
                    }
                }

                "for nullable property" - {
                    val property = JsObjectProperty.Nullable(nullable(PROPERTY_NAME, StringReader))
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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
                        val unknownProperty = JsObjectProperty.Nullable(nullable(UNKNOWN_PROPERTY_NAME, StringReader))

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
                    val property = JsObjectProperty.NullableWithDefault(
                        nullableWithDefault(PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
                    )
                    val map: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[property] = null
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
                        val unknownProperty = JsObjectProperty.NullableWithDefault(
                            nullableWithDefault(UNKNOWN_PROPERTY_NAME, StringReader, PROPERTY_DEFAULT)
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
}