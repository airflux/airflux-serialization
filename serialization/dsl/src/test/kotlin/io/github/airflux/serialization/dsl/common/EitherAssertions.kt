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

package io.github.airflux.serialization.dsl.common

import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal fun <L, R> Either<L, R>.shouldBeRight(): Either.Right<R> {
    contract {
        returns() implies (this@shouldBeRight is Either.Right<R>)
    }

    val message =
        "The result type is not as expected. Expected type: `Either.Right`, actual type: `Either.Left` ($this)."
    return if (this@shouldBeRight is Either.Right<R>) this else throw failure(message = message)
}

@OptIn(ExperimentalContracts::class)
internal fun <L, R> Either<L, R>.shouldBeLeft(): Either.Left<L> {
    contract {
        returns() implies (this@shouldBeLeft is Either.Left<L>)
    }

    val message =
        "The result type is not as expected. Expected type: `Either.Left`,  actual type: `Either.Right` ($this)."
    return if (this@shouldBeLeft is Either.Left<L>) this else throw failure(message = message)
}
