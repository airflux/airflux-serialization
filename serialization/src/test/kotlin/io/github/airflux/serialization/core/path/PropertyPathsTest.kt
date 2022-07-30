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

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class PropertyPathsTest : FreeSpec() {

    companion object {
        private val pathUser = PropertyPath("user")
        private val pathId = PropertyPath("id")
        private val pathName = PropertyPath("name")
    }

    init {

        "The PropertyPaths type" - {
            val paths = PropertyPaths(pathUser)

            "after creating" - {

                "should be non-empty" {
                    paths.items.isNotEmpty() shouldBe true
                }

                "should have size 1" {
                    paths.items.size shouldBe 1
                }

                "should have elements in the order they were passed" {
                    paths.items shouldContainExactly listOf(pathUser)
                }

                "method 'toString() should return '[$pathUser]'" {
                    paths.toString() shouldBe "[$pathUser]"
                }

                "and adding a unique path" - {
                    val updatedPaths = paths.append(pathId)

                    "should be non-empty" {
                        updatedPaths.items.isNotEmpty() shouldBe true
                    }

                    "should have size 2" {
                        updatedPaths.items.size shouldBe 2
                    }

                    "should have elements in the order they were passed" {
                        updatedPaths.items shouldContainExactly listOf(pathUser, pathId)
                    }

                    "method 'toString() should return '[$pathUser, $pathId]'" {
                        updatedPaths.toString() shouldBe "[$pathUser, $pathId]"
                    }
                }

                "and adding a non-unique path" - {
                    val updatedPaths = paths.append(pathUser)

                    "should return origin instance" {
                        updatedPaths shouldBe paths
                    }
                }

                "and adding a unique paths" - {
                    val updatedPaths = paths.append(PropertyPaths(pathId, pathName))

                    "should be non-empty" {
                        updatedPaths.items.isNotEmpty() shouldBe true
                    }

                    "should have size 3" {
                        updatedPaths.items.size shouldBe 3
                    }

                    "should have elements in the order they were passed" {
                        updatedPaths.items shouldContainExactly listOf(pathUser, pathId, pathName)
                    }

                    "method 'toString() should return '[$pathUser, $pathId, $pathName]'" {
                        updatedPaths.toString() shouldBe "[$pathUser, $pathId, $pathName]"
                    }
                }

                "and adding a non-unique paths" - {

                    "all adding elements is a non-unique" - {
                        val updatedPaths = paths.append(PropertyPaths(pathUser))

                        "should return origin instance" {
                            updatedPaths shouldBe paths
                        }
                    }

                    "some adding elements is a non-unique" - {
                        val updatedPaths = paths.append(PropertyPaths(pathUser, pathId))

                        "should be non-empty" {
                            updatedPaths.items.isNotEmpty() shouldBe true
                        }

                        "should have size 2" {
                            updatedPaths.items.size shouldBe 2
                        }

                        "should have elements in the order they were passed" {
                            updatedPaths.items shouldContainExactly listOf(pathUser, pathId)
                        }

                        "method 'toString() should return '[$pathUser, $pathId]'" {
                            updatedPaths.toString() shouldBe "[$pathUser, $pathId]"
                        }
                    }
                }
            }

            "calling the fold function should return a folding value" {
                val result = PropertyPaths(pathUser)
                    .append(pathId)
                    .fold({ path -> path.toString() }) { acc, path -> "$acc, $path" }

                result shouldBe "#/user, #/id"
            }
        }
    }
}
