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

package io.github.airflux.serialization.dsl.path

import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class PropertyPathsTest : FreeSpec() {

    companion object {
        private const val USER = "user"
        private const val ID = "id"
        private const val NAME = "name"

        private val USER_PATH = JsPath(USER)
        private val ID_PATH = JsPath(ID)
        private val NAME_PATH = JsPath(NAME)
    }

    init {
        "The PropertyPaths type" - {

            "String#or(String)" - {
                val paths = USER or ID
                paths.items shouldContainExactly listOf(USER_PATH, ID_PATH)
            }

            val paths = PropertyPaths(USER_PATH)

            "PropertyPaths#or(JsPath) function" - {
                val updatedPath = paths or ID_PATH

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(USER_PATH, ID_PATH)
                }
            }

            "PropertyPaths#or(PropertyPaths) function" - {
                val updatedPath = paths or PropertyPaths(ID_PATH, NAME_PATH)

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(USER_PATH, ID_PATH, NAME_PATH)
                }
            }
        }
    }
}
