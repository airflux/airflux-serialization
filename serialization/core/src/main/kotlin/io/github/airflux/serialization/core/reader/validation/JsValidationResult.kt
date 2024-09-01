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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed class JsValidationResult {
    public data object Valid : JsValidationResult()
    public data class Invalid(public val failure: JsReaderResult.Failure) : JsValidationResult()
}

@OptIn(ExperimentalContracts::class)
public fun JsValidationResult.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid is JsValidationResult.Valid)
        returns(false) implies (this@isValid is JsValidationResult.Invalid)
    }
    return this is JsValidationResult.Valid
}

@OptIn(ExperimentalContracts::class)
public fun JsValidationResult.isInvalid(): Boolean {
    contract {
        returns(false) implies (this@isInvalid is JsValidationResult.Valid)
        returns(true) implies (this@isInvalid is JsValidationResult.Invalid)
    }
    return this is JsValidationResult.Invalid
}

public fun JsValidationResult.getOrNull(): JsReaderResult.Failure? = if (isInvalid()) failure else null

@OptIn(ExperimentalContracts::class)
public inline fun <T> JsValidationResult.fold(onInvalid: (JsReaderResult.Failure) -> T, onValid: () -> T): T {
    contract {
        callsInPlace(onInvalid, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onValid, InvocationKind.AT_MOST_ONCE)
    }
    return if (isValid()) onValid() else onInvalid(failure)
}

@OptIn(ExperimentalContracts::class)
public inline fun JsValidationResult.ifInvalid(handler: (JsReaderResult.Failure) -> Unit) {
    contract {
        callsInPlace(handler, InvocationKind.AT_MOST_ONCE)
    }
    if (isInvalid()) handler(failure)
}

public fun valid(): JsValidationResult = JsValidationResult.Valid

public fun invalid(location: JsLocation, error: JsReaderResult.Error): JsValidationResult =
    invalid(JsReaderResult.Failure(location, error))

public fun invalid(failure: JsReaderResult.Failure): JsValidationResult = JsValidationResult.Invalid(failure)

public fun JsReaderResult.Failure.toInvalid(): JsValidationResult = invalid(this)
