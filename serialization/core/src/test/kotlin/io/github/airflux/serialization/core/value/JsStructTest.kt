/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsString
import io.github.airflux.serialization.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsStructTest : FreeSpec() {

    companion object {
        private const val USER_PROPERTY_NAME = "name"
        private const val USER_VALUE = "user"
        private val USER_PROPERTY_VALUE = JsString(USER_VALUE)

        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_VALUE = true
        private val IS_ACTIVE_PROPERTY_VALUE = JsBoolean.valueOf(IS_ACTIVE_VALUE)
    }

    init {
        "The JsStruct type" - {

            "when type value creating without properties" - {
                val struct = JsStruct()

                "should be empty" {
                    struct.isEmpty() shouldBe true
                }

                "should have count 0" {
                    struct.count shouldBe 0
                }

                "should not have elements" {
                    struct.shouldBeEmpty()
                }

                "then the method of getting the value of the element by key should return null" {
                    struct[USER_PROPERTY_NAME] shouldBe null
                }

                "then the method of getting the value of the element by path element should return null" {
                    struct[JsPath.Element.Key(USER_PROPERTY_NAME)] shouldBe null
                }

                "then the toString() method should return the expected string" {
                    struct shouldBeEqualsString "{}"
                }

                "should comply with equals() and hashCode() contract" {
                    struct.shouldBeEqualsContract(
                        y = JsStruct(),
                        z = JsStruct(),
                        other = JsStruct(USER_PROPERTY_NAME to USER_PROPERTY_VALUE)
                    )
                }
            }

            "when created with properties" - {
                val struct = JsStruct(
                    USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                    IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                )

                "should be non-empty" {
                    struct.isEmpty() shouldBe false
                }

                "should have count 2" {
                    struct.count shouldBe 2
                }

                "should have elements in the order they were added" {
                    struct shouldContainExactly listOf(
                        JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                        JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                    )
                }

                "then the method of getting the value of the element by key should return a specific value" - {
                    withData(
                        listOf(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        )
                    ) { (key, value) ->
                        struct[key] shouldBe value
                    }
                }

                "then the method of getting the value of the element by path element should return a specific value" - {
                    withData(
                        listOf(
                            JsPath.Element.Key(USER_PROPERTY_NAME) to USER_PROPERTY_VALUE,
                            JsPath.Element.Key(IS_ACTIVE_PROPERTY_NAME) to IS_ACTIVE_PROPERTY_VALUE
                        )
                    ) { (key, value) ->
                        struct[key] shouldBe value
                    }
                }

                "then the toString() method should return the expected string" {
                    struct.shouldBeEqualsString(
                        """{"$USER_PROPERTY_NAME": "$USER_VALUE", "$IS_ACTIVE_PROPERTY_NAME": $IS_ACTIVE_VALUE}"""
                    )
                }

                "should comply with equals() and hashCode() contract" {
                    struct.shouldBeEqualsContract(
                        y = JsStruct(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        ),
                        z = JsStruct(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        ),
                        others = listOf(
                            JsStruct(),
                            JsStruct(USER_PROPERTY_NAME to USER_PROPERTY_VALUE),
                            JsStruct(IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE)
                        )
                    )
                }
            }

            "when type value creating from a list of properties" - {
                val properties = listOf(
                    USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                    IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                )
                val struct = JsStruct(properties)

                "should be non-empty" {
                    struct.isEmpty() shouldBe false
                }

                "should have count 2" {
                    struct.count shouldBe 2
                }

                "should have elements in the order they were added" {
                    struct shouldContainExactly listOf(
                        JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                        JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                    )
                }

                "then the method of getting the value of the element by key should return a specific value" - {
                    withData(
                        listOf(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        )
                    ) { (key, value) ->
                        struct[key] shouldBe value
                    }
                }

                "then the method of getting the value of the element by path element should return a specific value" - {
                    withData(
                        listOf(
                            JsPath.Element.Key(USER_PROPERTY_NAME) to USER_PROPERTY_VALUE,
                            JsPath.Element.Key(IS_ACTIVE_PROPERTY_NAME) to IS_ACTIVE_PROPERTY_VALUE
                        )
                    ) { (key, value) ->
                        struct[key] shouldBe value
                    }
                }

                "then the toString() method should return the expected string" {
                    struct.shouldBeEqualsString(
                        """{"$USER_PROPERTY_NAME": "$USER_VALUE", "$IS_ACTIVE_PROPERTY_NAME": $IS_ACTIVE_VALUE}"""
                    )
                }

                "should comply with equals() and hashCode() contract" {
                    struct.shouldBeEqualsContract(
                        y = JsStruct(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        ),
                        z = JsStruct(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        ),
                        others = listOf(
                            JsStruct(),
                            JsStruct(USER_PROPERTY_NAME to USER_PROPERTY_VALUE),
                            JsStruct(IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE)
                        )
                    )
                }
            }
        }
    }
}
