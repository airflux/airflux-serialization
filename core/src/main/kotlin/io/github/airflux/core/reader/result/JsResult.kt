/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.core.reader.result

import io.github.airflux.core.common.identity

@Suppress("unused")
sealed class JsResult<out T> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R> =
        flatMap { location, value -> transform(value).success(location) }

    fun <R> flatMap(transform: (JsLocation, T) -> JsResult<R>): JsResult<R> = fold(
        ifFailure = ::identity,
        ifSuccess = { transform(it.location, it.value) }
    )

    data class Success<T>(val location: JsLocation, val value: T) : JsResult<T>()

    class Failure private constructor(val causes: List<Cause>) : JsResult<Nothing>() {

        constructor(location: JsLocation, error: JsError) : this(listOf(Cause(location, error)))

        constructor(location: JsLocation, errors: JsErrors) : this(listOf(Cause(location, errors)))

        operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        override fun toString(): String = buildString {
            append("Failure(causes=")
            append(causes.toString())
            append(")")
        }

        data class Cause(val location: JsLocation, val errors: JsErrors) {
            constructor(location: JsLocation, error: JsError) : this(location, JsErrors.of(error))
        }

        companion object {
            fun Collection<Failure>.merge(): Failure = Failure(causes = flatMap { failure -> failure.causes })
        }
    }
}

inline fun <T, R> JsResult<T>.fold(ifFailure: (JsResult.Failure) -> R, ifSuccess: (JsResult.Success<T>) -> R): R =
    when (this) {
        is JsResult.Success -> ifSuccess(this)
        is JsResult.Failure -> ifFailure(this)
    }

inline infix fun <T> JsResult<T>.recovery(function: (JsResult.Failure) -> JsResult<T>): JsResult<T> = fold(
    ifFailure = { function(it) },
    ifSuccess = ::identity
)

infix fun <T> JsResult<T>.getOrElse(defaultValue: () -> T): T = fold(
    ifFailure = { defaultValue() },
    ifSuccess = { it.value }
)

infix fun <T> JsResult<T>.orElse(defaultValue: () -> JsResult<T>): JsResult<T> = fold(
    ifFailure = { defaultValue() },
    ifSuccess = ::identity
)

fun <T> T.success(location: JsLocation): JsResult<T> = JsResult.Success(location, this)
fun <E : JsError> E.failure(location: JsLocation): JsResult<Nothing> = JsResult.Failure(location, this)
