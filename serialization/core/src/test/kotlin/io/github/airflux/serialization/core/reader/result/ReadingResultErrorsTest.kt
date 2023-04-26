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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class ReadingResultErrorsTest : FreeSpec() {

    companion object {
        private val FIRST_ERROR = JsonErrors.PathMissing
        private val SECOND_ERROR = JsonErrors.AdditionalItems
    }

    init {
        "The ReadingResult#Errors type" - {

            "when the value of the Errors type is created with only one item" - {
                val errors = ReadingResult.Errors(FIRST_ERROR)

                "then the value should have only the passed element" {
                    errors.items shouldContainExactly listOf(FIRST_ERROR)
                }

                "when to the value appended other errors" - {
                    val mergedErrors = errors + ReadingResult.Errors(SECOND_ERROR)

                    "then the new value should have all elements" {
                        mergedErrors.items shouldContainExactly listOf(
                            FIRST_ERROR,
                            SECOND_ERROR
                        )
                    }
                }
            }

            "should comply with equals() and hashCode() contract" {
                ReadingResult.Errors(FIRST_ERROR).shouldBeEqualsContract(
                    y = ReadingResult.Errors(FIRST_ERROR),
                    z = ReadingResult.Errors(FIRST_ERROR),
                    others = listOf(
                        ReadingResult.Errors(FIRST_ERROR).plus(
                            ReadingResult.Errors(
                                SECOND_ERROR
                            )
                        ),
                        ReadingResult.Errors(SECOND_ERROR)
                    )
                )
            }
        }
    }
}
