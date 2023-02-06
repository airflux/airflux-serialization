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

package io.github.airflux.serialization.core.common.kotest

import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal infix fun <T> ReaderResult<T?>.shouldBeSuccess(value: ReaderResult.Success<T?>) {
    val result = this.shouldBeInstanceOf<ReaderResult.Success<T>>()
    result shouldBe value
}

internal infix fun ReaderResult<*>.shouldBeFailure(expected: ReaderResult.Failure) {
    val failure = this.shouldBeInstanceOf<ReaderResult.Failure>()
    failure shouldBe expected
}

internal fun ReaderResult<*>.shouldBeFailure(vararg expected: ReaderResult.Failure.Cause) {
    val failure = this.shouldBeInstanceOf<ReaderResult.Failure>()
    failure.causes shouldContainExactly expected.toList()
}
