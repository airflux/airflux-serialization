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

package io.github.airflux.serialization.dsl.path

import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class PropertyPathsOpsTest : FreeSpec() {

    companion object {
        private val pathUser = PropertyPath("user")
        private val pathId = PropertyPath("id")
        private val pathName = PropertyPath("name")
    }

    init {
        "The PropertyPaths type" - {

            "String#or(String)" - {
                val paths = "user" or "id"
                paths.items shouldContainExactly listOf(pathUser, pathId)
            }

            val paths = PropertyPaths(pathUser)

            "PropertyPaths#or(PropertyPath) function" - {
                val updatedPath = paths or pathId

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(pathUser, pathId)
                }
            }

            "PropertyPaths#or(PropertyPaths) function" - {
                val updatedPath = paths or PropertyPaths(pathId, pathName)

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(pathUser, pathId, pathName)
                }
            }
        }
    }
}
