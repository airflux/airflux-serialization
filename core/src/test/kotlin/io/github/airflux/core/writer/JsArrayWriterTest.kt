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

package io.github.airflux.core.writer

import io.github.airflux.common.DummyArrayWriter
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.ActionOfWriterIfArrayIsEmpty
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayWriterTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM_VALUE = "first"
        private const val SECOND_ITEM_VALUE = "second"
        private val COLLECTION = listOf(FIRST_ITEM_VALUE, SECOND_ITEM_VALUE)

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The actionIfEmpty extension function" - {

            "when the basic writer returns is the null value" - {
                val writer: JsArrayWriter<String> = DummyArrayWriter<String> { null }
                    .actionIfEmpty()

                "then the method write should return the null value" {
                    val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                    result.shouldBeNull()
                }
            }

            "when the basic writer returns is the JsNull value" - {
                val writer: JsArrayWriter<String> = DummyArrayWriter<String> { JsNull }
                    .actionIfEmpty()

                "then the method write should return the JsNull value" {
                    val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                    result shouldBe JsNull
                }
            }

            "when the basic writer returns is not empty array" - {
                val writer: JsArrayWriter<String> =
                    DummyArrayWriter<String> { JsArray(JsString(FIRST_ITEM_VALUE), JsString(SECOND_ITEM_VALUE)) }
                        .actionIfEmpty()

                "then the method write should return the not empty array" {
                    val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                    result shouldBe JsArray(JsString(FIRST_ITEM_VALUE), JsString(SECOND_ITEM_VALUE))
                }
            }

            "when the basic writer returns is empty array" - {
                val baseWriter = DummyArrayWriter<String> { JsArray<Nothing>() }

                "when selector for actionIfEmpty is not passed" - {
                    val writer: JsArrayWriter<String> = baseWriter.actionIfEmpty()

                    "then the method write should return the null value" {
                        val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                        result.shouldBeNull()
                    }
                }

                "when selector for actionIfEmpty function is passed and it returns the action is NONE " - {
                    val writer: JsArrayWriter<String> = baseWriter.actionIfEmpty { _, _ ->
                        ActionOfWriterIfArrayIsEmpty.Action.NONE
                    }

                    "then the method write should return the empty array" {
                        val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                        result shouldBe JsArray<Nothing>()
                    }
                }

                "when selector for actionIfEmpty function is passed and it returns the action is NULL " - {
                    val writer: JsArrayWriter<String> = baseWriter.actionIfEmpty { _, _ ->
                        ActionOfWriterIfArrayIsEmpty.Action.NULL
                    }

                    "then the method write should return the JsNull value" {
                        val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                        result shouldBe JsNull
                    }
                }

                "when selector for actionIfEmpty function is passed and it returns the action is SKIP " - {
                    val writer: JsArrayWriter<String> = baseWriter.actionIfEmpty { _, _ ->
                        ActionOfWriterIfArrayIsEmpty.Action.SKIP
                    }

                    "then the method write should return the null value" {
                        val result = writer.write(CONTEXT, LOCATION, COLLECTION)
                        result.shouldBeNull()
                    }
                }
            }
        }
    }
}
