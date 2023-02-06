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

package io.github.airflux.serialization.core.reader.predicate

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class ReaderPredicateCombinatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty

        private const val MIN_VALUE = 10
        private const val MAX_VALUE = 20
    }

    init {
        "The 'AND' combinator" - {
            val leftFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, _, value -> value > MIN_VALUE }
            val rightFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, _, value -> value < MAX_VALUE }
            val composedFilter = leftFilter and rightFilter
            withData(
                nameFn = { it.first },
                listOf(
                    Triple("the value is less than the minimum value of the range", MIN_VALUE - 1, false),
                    Triple("the value is equal to the minimum value of the range", MIN_VALUE, false),
                    Triple("the value is more than the minimum value of the range", MIN_VALUE + 1, true),
                    Triple("the value is less than the maximum value of the range", MAX_VALUE - 1, true),
                    Triple("the value is equal to the maximum value of the range", MAX_VALUE, false),
                    Triple("the value is more than the maximum value of the range", MAX_VALUE + 1, false)
                )
            ) { (_, value, expected) ->
                val result = composedFilter.test(ENV, CONTEXT, LOCATION, value)
                result shouldBe expected
            }
        }

        "The 'OR' combinator" - {
            val leftFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, _, value -> value < MIN_VALUE }
            val rightFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, _, value -> value > MAX_VALUE }
            val composedFilter = leftFilter or rightFilter
            withData(
                nameFn = { it.first },
                listOf(
                    Triple("the value is less than the minimum value of the range", MIN_VALUE - 1, true),
                    Triple("the value is equal to the minimum value of the range", MIN_VALUE, false),
                    Triple("the value is more than the minimum value of the range", MIN_VALUE + 1, false),
                    Triple("the value is less than the maximum value of the range", MAX_VALUE - 1, false),
                    Triple("the value is equal to the maximum value of the range", MAX_VALUE, false),
                    Triple("the value is more than the maximum value of the range", MAX_VALUE + 1, true)
                )
            ) { (_, value, expected) ->
                val result = composedFilter.test(ENV, CONTEXT, LOCATION, value)
                result shouldBe expected
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
