/*
 * Copyright 2021-2024 Maxim Sambulat.
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

import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.isError
import io.github.airflux.serialization.core.reader.result.isSuccess
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <reified T> JsReaderResult<T>.shouldBeSuccess(): JsReaderResult.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is JsReaderResult.Success<T>)
    }

    if (this.isError()) {
        errorCollector.collectOrThrow(
            failure(
                expected = JsReaderResult.Success::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Success, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as JsReaderResult.Success<T>
}

public inline infix fun <reified T> JsReaderResult<T>.shouldBeSuccess(expected: JsReaderResult<T>) {
    this.shouldBeSuccess() shouldBe expected
}

@OptIn(ExperimentalContracts::class)
public fun JsReaderResult<*>.shouldBeFailure(): JsReaderResult.Failure {
    contract {
        returns() implies (this@shouldBeFailure is JsReaderResult.Failure)
    }

    if (this.isSuccess()) {
        errorCollector.collectOrThrow(
            failure(
                expected = JsReaderResult.Failure::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Failure, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as JsReaderResult.Failure
}

public infix fun JsReaderResult<*>.shouldBeFailure(expected: JsReaderResult<*>) {
    this.shouldBeFailure() shouldBe expected
}

public fun JsReaderResult<*>.shouldBeFailure(vararg expected: JsReaderResult.Failure.Cause) {
    val failure = this.shouldBeFailure()
    failure.causes.items shouldContainExactly expected.toList()
}
