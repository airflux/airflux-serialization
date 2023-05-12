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
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class JsStructTest : FreeSpec() {

    companion object {
        private const val USER_PROPERTY_NAME = "name"
        private const val USER_VALUE = "user"
        private val USER_PROPERTY_VALUE = JsString(USER_VALUE)
        private const val OTHER_USER_VALUE = "user"
        private val OTHER_USER_PROPERTY_VALUE = JsString(OTHER_USER_VALUE)

        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_VALUE = true
        private val IS_ACTIVE_PROPERTY_VALUE = JsBoolean.valueOf(IS_ACTIVE_VALUE)

        private const val EMAIL_PROPERTY_NAME = "email"
        private const val EMAIL_VALUE = "user@example.com"
        private val EMAIL_PROPERTY_VALUE = JsString(EMAIL_VALUE)
    }

    init {
        "The JsStruct type" - {

            "The JsStruct#Builder" - {

                "the `contains` method" - {
                    val builder = JsStruct.builder()
                        .apply { put(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE) }

                    "then should return `true` if the given name is among the properties" - {
                        builder.contains(USER_PROPERTY_NAME) shouldBe true
                    }

                    "then should return `false` if the given name is not among the properties" - {
                        builder.contains(IS_ACTIVE_PROPERTY_NAME) shouldBe false
                    }
                }

                "the `put` method" - {

                    "then should add the element if it does not exist" - {
                        val struct = JsStruct.builder()
                            .apply { put(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE) }
                            .build()

                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                        )
                    }

                    "then should replace the element if it exists" - {
                        val struct = JsStruct.builder()
                            .apply {
                                put(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE)
                                put(name = USER_PROPERTY_NAME, value = OTHER_USER_PROPERTY_VALUE)
                            }
                            .build()

                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = OTHER_USER_PROPERTY_VALUE),
                        )
                    }
                }

                "the `putAll` method" - {

                    "then should add the element if it does not exist" - {
                        val struct = JsStruct.builder()
                            .apply {
                                putAll(
                                    listOf(
                                        USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                                        IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                                    )
                                )
                            }
                            .build()

                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE),
                        )
                    }

                    "then should replace the element if it exists" - {
                        val struct = JsStruct.builder()
                            .apply {
                                put(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE)
                                putAll(
                                    listOf(
                                        USER_PROPERTY_NAME to OTHER_USER_PROPERTY_VALUE,
                                        IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                                    )
                                )
                            }
                            .build()

                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = OTHER_USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE),
                        )
                    }
                }
            }

            "when the struct is empty" - {
                val struct = JsStruct()

                "then the `isEmpty` method should return `true` value" {
                    struct.isEmpty() shouldBe true
                }

                "then the `count` property should return the value is 0" {
                    struct.count shouldBe 0
                }

                "then the `contains` method for the key should return `false` value" {
                    (USER_PROPERTY_NAME in struct) shouldBe false
                }

                "then the `contains` method for the path element should return `false` value" {
                    (JsPath.Element.Key(USER_PROPERTY_NAME) in struct) shouldBe false
                }

                "then the `get` method for the key should return null" {
                    struct[USER_PROPERTY_NAME] shouldBe null
                }

                "then the `get` method for the path element should return null" {
                    struct[JsPath.Element.Key(USER_PROPERTY_NAME)] shouldBe null
                }

                "then the `toString` method should return the expected string" {
                    struct shouldBeEqualsString "{}"
                }

                "then should comply with equals() and hashCode() contract" {
                    struct.shouldBeEqualsContract(
                        y = JsStruct(),
                        z = JsStruct(),
                        other = JsStruct(USER_PROPERTY_NAME to USER_PROPERTY_VALUE)
                    )
                }
            }

            "when the struct is not empty" - {
                val struct = JsStruct(
                    USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                    IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                )

                "then the `isEmpty` method should return `false` value" {
                    struct.isEmpty() shouldBe false
                }

                "then the `count` property should return the value is 2" {
                    struct.count shouldBe 2
                }

                "then the `contains` method for the key" - {

                    "then should return `true` value if the element exists" - {
                        withData(
                            listOf(USER_PROPERTY_NAME, IS_ACTIVE_PROPERTY_NAME)
                        ) { key ->
                            (key in struct) shouldBe true
                        }
                    }

                    "then should return `false` value if the element not exists" {
                        (EMAIL_PROPERTY_NAME in struct) shouldBe false
                    }
                }

                "then the `contains` method for the path element" - {

                    "then should return `true` value if the element exists" - {
                        withData(
                            listOf(
                                JsPath.Element.Key(USER_PROPERTY_NAME),
                                JsPath.Element.Key(IS_ACTIVE_PROPERTY_NAME)
                            )
                        ) { key ->
                            (key in struct) shouldBe true
                        }

                    }

                    "then should return `false` value if the element not exists" {
                        (JsPath.Element.Key(EMAIL_PROPERTY_NAME) in struct) shouldBe false
                    }
                }

                "then the `get` method for the key should return a specific value" - {
                    withData(
                        listOf(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        )
                    ) { (key, value) ->
                        struct[key] shouldBe value
                    }
                }

                "then the `get` method for the path element should return a specific value" - {
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

                "then should comply with equals() and hashCode() contract" {
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
                            JsStruct(IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE),
                            JsStruct(EMAIL_PROPERTY_NAME to EMAIL_PROPERTY_VALUE),
                            JsStruct(
                                USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                                IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE,
                                EMAIL_PROPERTY_NAME to EMAIL_PROPERTY_VALUE
                            )
                        )
                    )
                }
            }

            "when creating a type value without properties" - {
                val struct = JsStruct()

                "then should not have elements" {
                    struct.shouldBeEmpty()
                }
            }

            "when creating a type value with properties" - {

                "when properties do not have name duplicates" - {
                    val struct = JsStruct(
                        USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                        IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                    )

                    "then should contain all passed elements" {
                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                        )
                    }
                }

                "when properties have name duplicates" - {
                    val struct = JsStruct(
                        USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                        USER_PROPERTY_NAME to OTHER_USER_PROPERTY_VALUE,
                        IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                    )

                    "then should contain all unique elements" {
                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = OTHER_USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                        )
                    }
                }
            }

            "when creating a type value from a list of properties" - {

                "when the list of properties does not have name duplicates" - {
                    val struct = JsStruct(
                        listOf(
                            USER_PROPERTY_NAME to USER_PROPERTY_VALUE,
                            IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                        )
                    )

                    "then should contain all passed elements" {
                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                        )
                    }
                }

                "when the list of properties has name duplicates" - {
                    val properties = listOf(
                        USER_PROPERTY_NAME to OTHER_USER_PROPERTY_VALUE,
                        IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE
                    )
                    val struct = JsStruct(properties)

                    "then should contain all unique elements" {
                        struct.toList() shouldContainExactlyInAnyOrder listOf(
                            JsStruct.Property(name = USER_PROPERTY_NAME, value = OTHER_USER_PROPERTY_VALUE),
                            JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE)
                        )
                    }
                }
            }

            "when creating a type value using the builder" - {
                val struct = JsStruct.builder()
                    .apply {
                        put(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE)
                        putAll(
                            listOf(
                                IS_ACTIVE_PROPERTY_NAME to IS_ACTIVE_PROPERTY_VALUE,
                                EMAIL_PROPERTY_NAME to EMAIL_PROPERTY_VALUE
                            )
                        )
                    }
                    .build()

                "then should contain all passed elements" {
                    struct.toList() shouldContainExactlyInAnyOrder listOf(
                        JsStruct.Property(name = USER_PROPERTY_NAME, value = USER_PROPERTY_VALUE),
                        JsStruct.Property(name = IS_ACTIVE_PROPERTY_NAME, value = IS_ACTIVE_PROPERTY_VALUE),
                        JsStruct.Property(name = EMAIL_PROPERTY_NAME, value = EMAIL_PROPERTY_VALUE)
                    )
                }
            }
        }
    }
}
