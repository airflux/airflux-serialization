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

package io.github.airflux.serialization.test.kotest

import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

public infix fun <T> ReadingResult<T?>.shouldBeSuccess(expected: ReadingResult<T?>) {
    val actualResult = this.shouldBeInstanceOf<ReadingResult.Success<T>>()
    val expectedResult = expected.shouldBeInstanceOf<ReadingResult.Success<T>>()
    actualResult shouldBe expectedResult
}

public infix fun ReadingResult<*>.shouldBeFailure(expected: ReadingResult<*>) {
    val actualFailure = this.shouldBeInstanceOf<ReadingResult.Failure>()
    val expectedFailure = expected.shouldBeInstanceOf<ReadingResult.Failure>()
    actualFailure shouldBe expectedFailure
}

public fun ReadingResult<*>.shouldBeFailure(vararg expected: ReadingResult.Failure.Cause) {
    val failure = this.shouldBeInstanceOf<ReadingResult.Failure>()
    failure.causes.items shouldContainExactly expected.toList()
}
