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

import io.github.airflux.serialization.core.reader.validation.JsValidationResult
import io.github.airflux.serialization.core.reader.validation.isInvalid
import io.github.airflux.serialization.core.reader.validation.isValid
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun JsValidationResult.shouldBeValid(
    message: (JsValidationResult.Invalid) -> String = { it.toString() }
): JsValidationResult.Valid {
    contract {
        returns() implies (this@shouldBeValid is JsValidationResult.Valid)
    }

    if (this.isInvalid()) {
        val expectedType = JsValidationResult.Valid::class.qualifiedName!!
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
    return this as JsValidationResult.Valid
}

@OptIn(ExperimentalContracts::class)
public fun JsValidationResult.shouldBeInvalid(
    message: () -> String = { "" }
): JsValidationResult.Invalid {
    contract {
        returns() implies (this@shouldBeInvalid is JsValidationResult.Invalid)
    }

    if (this.isValid()) {
        val expectedType = JsValidationResult.Invalid::class.qualifiedName!!
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
    return this as JsValidationResult.Invalid
}

public infix fun JsValidationResult.shouldBeInvalid(expected: JsValidationResult) {
    val actual = this.shouldBeInvalid()
    actual shouldBe expected
}

public fun JsValidationResult.shouldBeInvalid(expected: JsValidationResult, message: () -> String) {
    val actual = this.shouldBeInvalid(message)
    actual shouldBe expected
}
