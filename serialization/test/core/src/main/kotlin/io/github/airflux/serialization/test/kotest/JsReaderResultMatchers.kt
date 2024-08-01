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
public inline fun <reified T> JsReaderResult<T>.shouldBeSuccess(
    message: (JsReaderResult.Failure) -> String = { it.toString() }
): JsReaderResult.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is JsReaderResult.Success<T>)
    }

    if (this.isError()) {
        val expectedType = JsReaderResult.Success::class.qualifiedName!!
        val actualType = this::class.qualifiedName!!
        val causeDescription = message(this).makeDescription()
        errorCollector.collectOrThrow(
            failure(
                expected = expectedType,
                actual = actualType,
                failureMessage = "Expected the `$expectedType` type, but got the `$actualType` type$causeDescription"
            )
        )
    }
    return this as JsReaderResult.Success<T>
}

public inline infix fun <reified T> JsReaderResult<T>.shouldBeSuccess(expected: JsReaderResult<T>) {
    val actual = this.shouldBeSuccess { failure -> failure.toString() }
    actual shouldBe expected
}

public inline fun <reified T> JsReaderResult<T>.shouldBeSuccess(
    expected: JsReaderResult<T>,
    message: (JsReaderResult.Failure) -> String
) {
    val actual = this.shouldBeSuccess(message)
    actual shouldBe expected
}

@OptIn(ExperimentalContracts::class)
public fun JsReaderResult<*>.shouldBeFailure(
    message: (JsReaderResult.Success<*>) -> String = { it.toString() }
): JsReaderResult.Failure {
    contract {
        returns() implies (this@shouldBeFailure is JsReaderResult.Failure)
    }

    if (this.isSuccess()) {
        val expectedType = JsReaderResult.Failure::class.qualifiedName!!
        val actualType = this::class.simpleName!!
        val causeDescription = message(this).makeDescription()
        errorCollector.collectOrThrow(
            failure(
                expected = expectedType,
                actual = actualType,
                failureMessage = "Expected the `$expectedType` type, but got the `$actualType` type$causeDescription"
            )
        )
    }
    return this as JsReaderResult.Failure
}

public infix fun JsReaderResult<*>.shouldBeFailure(expected: JsReaderResult<*>) {
    val actual = this.shouldBeFailure { success -> success.toString() }
    actual shouldBe expected
}

public fun JsReaderResult<*>.shouldBeFailure(
    expected: JsReaderResult<*>,
    message: (JsReaderResult.Success<*>) -> String
) {
    val actual = this.shouldBeFailure(message)
    actual shouldBe expected
}

public fun JsReaderResult<*>.shouldBeFailure(
    vararg expected: JsReaderResult.Failure.Cause,
    message: (JsReaderResult.Success<*>) -> String = { it.toString() }
) {
    val failure = this.shouldBeFailure(message)
    failure.causes.items shouldContainExactly expected.toList()
}

@PublishedApi
internal fun String.makeDescription(): String = escape()
    .takeIf { it.isNotBlank() }
    ?.let { " ($it)." }
    ?: "."

@PublishedApi
internal fun String.escape(): String = this.replace(System.lineSeparator(), ". ")
