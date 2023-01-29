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

package io.github.airflux.serialization.core.path

import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class PropertyPathsTest : FreeSpec() {

    companion object {
        private const val FIRST_PROPERTY_NAME = "first"
        private val FIRST_PATH = PropertyPath(FIRST_PROPERTY_NAME)

        private const val SECOND_PROPERTY_NAME = "second"
        private val SECOND_PATH = PropertyPath(SECOND_PROPERTY_NAME)
    }

    init {

        "The PropertyPaths type" - {

            "when the value of the PropertyPaths type is created with only one item" - {
                val paths = PropertyPaths(FIRST_PATH)

                "then the value should have only the passed element" {
                    paths.items shouldContainExactly listOf(FIRST_PATH)
                }

                "method 'toString() should return '[$FIRST_PROPERTY_NAME]'" {
                    paths.toString() shouldBe "[#/$FIRST_PROPERTY_NAME]"
                }
            }

            "when the value of the PropertyPaths type is created with two items" - {
                val paths = PropertyPaths(FIRST_PATH, SECOND_PATH)

                "then the value should have only the passed elements" {
                    paths.items shouldContainExactly listOf(FIRST_PATH, SECOND_PATH)
                }

                "method 'toString() should return '[$FIRST_PROPERTY_NAME, $SECOND_PROPERTY_NAME]'" {
                    paths.toString() shouldBe "[#/$FIRST_PROPERTY_NAME, #/$SECOND_PROPERTY_NAME]"
                }
            }

            "when some path is added to the value of the PropertyPaths type" - {
                val paths = PropertyPaths(FIRST_PATH)

                "when another path is unique" - {
                    val mergedPaths = paths.append(SECOND_PATH)

                    "then the new value should have all elements" {
                        mergedPaths.items shouldContainExactly listOf(FIRST_PATH, SECOND_PATH)
                    }
                }

                "when another path is not unique" - {
                    val mergedPaths = paths.append(FIRST_PATH)

                    "then the same instance should be returned" {
                        mergedPaths.items shouldBeSameInstanceAs paths.items
                    }
                }
            }

            "when some paths are added to the value of the PropertyPaths type" - {
                val paths = PropertyPaths(FIRST_PATH)

                "when added paths are unique" - {
                    val mergedPaths = paths.append(PropertyPaths(SECOND_PATH))

                    "then the new value should have all elements" {
                        mergedPaths.items shouldContainExactly listOf(FIRST_PATH, SECOND_PATH)
                    }
                }

                "when added paths are not unique" - {
                    val mergedPaths = paths.append(PropertyPaths(FIRST_PATH))

                    "then the same instance should be returned" {
                        mergedPaths.items shouldBeSameInstanceAs paths.items
                    }
                }
            }

            "should comply with equals() and hashCode() contract" {
                PropertyPaths(FIRST_PATH).shouldBeEqualsContract(
                    y = PropertyPaths(FIRST_PATH),
                    z = PropertyPaths(FIRST_PATH),
                    other = listOf(
                        PropertyPaths(SECOND_PATH),
                        PropertyPaths(FIRST_PATH).append(SECOND_PATH)
                    )
                )
            }
        }
    }
}
