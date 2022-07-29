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

import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsPathsOpsTest : FreeSpec() {

    companion object {
        private val pathUser = JsPath("user")
        private val pathId = JsPath("id")
        private val pathName = JsPath("name")
    }

    init {
        "The JsPaths type" - {

            "String#or(String)" - {
                val paths = "user" or "id"
                paths.items shouldContainExactly listOf(
                    io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathUser,
                    io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathId
                )
            }

            val paths = JsPaths(io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathUser)

            "JsPaths#or(JsPath) function" - {
                val updatedPath = paths or io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathId

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(
                        io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathUser,
                        io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathId
                    )
                }
            }

            "JsPaths#or(JsPaths) function" - {
                val updatedPath = paths or JsPaths(
                    io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathId,
                    io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathName
                )

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainExactly listOf(
                        io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathUser,
                        io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathId,
                        io.github.airflux.serialization.dsl.path.JsPathsOpsTest.Companion.pathName
                    )
                }
            }
        }
    }
}