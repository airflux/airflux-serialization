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

import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.isInvalid
import io.github.airflux.serialization.core.reader.validation.isValid
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun JsValidatorResult.shouldBeValid(): JsValidatorResult.Valid {
    contract {
        returns() implies (this@shouldBeValid is JsValidatorResult.Valid)
    }

    if (this.isInvalid()) {
        errorCollector.collectOrThrow(
            failure(
                expected = JsValidatorResult.Valid::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Valid, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as JsValidatorResult.Valid
}

@OptIn(ExperimentalContracts::class)
public fun JsValidatorResult.shouldBeInvalid(): JsValidatorResult.Invalid {
    contract {
        returns() implies (this@shouldBeInvalid is JsValidatorResult.Invalid)
    }

    if (this.isValid()) {
        errorCollector.collectOrThrow(
            failure(
                expected = JsValidatorResult.Invalid::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Invalid, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as JsValidatorResult.Invalid
}

public infix fun JsValidatorResult.shouldBeInvalid(expected: JsReaderResult<*>) {
    val actual = this.shouldBeInvalid()
    actual.failure shouldBe expected
}
