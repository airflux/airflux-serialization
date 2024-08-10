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

package io.github.airflux.serialization.kotest.assertions

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.isFailure
import io.github.airflux.serialization.core.reader.result.isSuccess
import io.github.airflux.serialization.core.reader.result.plus
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
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

    if (this.isFailure()) {
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

public inline fun <reified T> JsReaderResult<T>.shouldBeSuccess(
    location: JsLocation,
    value: T,
    message: (JsReaderResult.Failure) -> String = { it.toString() }
) {
    this.shouldBeSuccess(message) shouldBe JsReaderResult.Success(location, value)
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

public fun JsReaderResult<*>.shouldBeFailure(
    cause: JsReaderResult.Failure.Cause,
    vararg causes: JsReaderResult.Failure.Cause,
    message: (JsReaderResult.Success<*>) -> String = { it.toString() }
) {
    val actual = this.shouldBeFailure(message)
//    val expected = JsReaderResult.Failure(JsReaderResult.Failure.Causes(cause, causes.toList()))
    val expected = causes.fold(JsReaderResult.Failure(cause)) { acc, item ->
        acc + JsReaderResult.Failure(item)
    }
    actual shouldBe expected
}

public fun JsReaderResult<*>.shouldBeFailure(
    location: JsLocation,
    error: JsReaderResult.Error,
    message: (JsReaderResult.Success<*>) -> String = { it.toString() }
) {
    this.shouldBeFailure(cause = cause(location = location, error = error), message = message)
}

public fun cause(location: JsLocation, error: JsReaderResult.Error): JsReaderResult.Failure.Cause =
    JsReaderResult.Failure.Cause(location, error)
