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

package io.github.airflux.serialization.core.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.test.dummy.DummyWriter
import io.kotest.assertions.failure
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class WriterFilterTest : FreeSpec() {

    companion object {
        private const val PROPERTY_VALUE = "89ec69f1-c636-42b8-8e62-6250c4321330"

        private val ENV = JsWriterEnv(options = Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
    }

    init {
        "The Writer type" - {

            "when the filter was added to the writer" - {
                val writer: JsWriter<Unit, Unit, String?> = DummyWriter<Unit, Unit, String?> { JsString(it!!) }

                "when the value is not null" - {
                    val value = PROPERTY_VALUE

                    "when passing a value that satisfies the predicate for filtering" - {
                        val writerWithFilter = writer.filter { _, _, _, _ -> true }

                        "then the writer should return non-null value" {
                            val result = writerWithFilter.write(ENV, CONTEXT, LOCATION, value)
                            result shouldBe JsString(PROPERTY_VALUE)
                        }
                    }

                    "when passing a value that does not satisfy the filter predicate" - {
                        val writerWithFilter = writer.filter { _, _, _, _ -> false }

                        "then the writer should return null value" {
                            val result = writerWithFilter.write(ENV, CONTEXT, LOCATION, value)
                            result.shouldBeNull()
                        }
                    }
                }

                "when the value is null" - {
                    val value: String? = null

                    "when passing a value that satisfies the predicate for filtering" - {
                        val writerWithFilter = writer.filter { _, _, _, _ -> throw failure("Predicate not called.") }

                        "then the filter should not be applying" {
                            val result = writerWithFilter.write(ENV, CONTEXT, LOCATION, value)
                            result.shouldBeNull()
                        }
                    }

                    "when passing a value that does not satisfy the filter predicate" - {
                        val writerWithFilter = writer.filter { _, _, _, _ -> throw failure("Predicate not called.") }

                        "then the filter should not be applying" {
                            val result = writerWithFilter.write(ENV, CONTEXT, LOCATION, value)
                            result.shouldBeNull()
                        }
                    }
                }
            }
        }
    }
}
