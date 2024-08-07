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

import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.isInvalid
import io.github.airflux.serialization.core.reader.validation.isValid
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun JsValidatorResult.shouldBeValid(
    message: (JsValidatorResult.Invalid) -> String = { it.toString() }
): JsValidatorResult.Valid {
    contract {
        returns() implies (this@shouldBeValid is JsValidatorResult.Valid)
    }

    if (this.isInvalid()) {
        val expectedType = JsValidatorResult.Valid::class.qualifiedName!!
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
    return this as JsValidatorResult.Valid
}

@OptIn(ExperimentalContracts::class)
public fun JsValidatorResult.shouldBeInvalid(
    message: () -> String = { "" }
): JsValidatorResult.Invalid {
    contract {
        returns() implies (this@shouldBeInvalid is JsValidatorResult.Invalid)
    }

    if (this.isValid()) {
        val expectedType = JsValidatorResult.Invalid::class.qualifiedName!!
        val actualType = this::class.qualifiedName!!
        val causeDescription = message().makeDescription()

        errorCollector.collectOrThrow(
            failure(
                expected = expectedType,
                actual = actualType,
                failureMessage = "Expected the `$expectedType` type, but got the `$actualType` type$causeDescription"
            )
        )
    }
    return this as JsValidatorResult.Invalid
}

public infix fun JsValidatorResult.shouldBeInvalid(expected: JsValidatorResult) {
    val actual = this.shouldBeInvalid()
    actual shouldBe expected
}

public fun JsValidatorResult.shouldBeInvalid(expected: JsValidatorResult, message: () -> String) {
    val actual = this.shouldBeInvalid(message)
    actual shouldBe expected
}
