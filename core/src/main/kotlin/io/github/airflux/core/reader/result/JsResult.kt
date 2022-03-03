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

@Suppress("unused")
sealed class JsResult<out T> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R> = when (this) {
        is Success -> Success(location, transform(value))
        is Failure -> this
    }

    fun <R> flatMap(transform: (JsLocation, T) -> JsResult<R>): JsResult<R> = when (this) {
        is Success -> transform(this.location, value)
        is Failure -> this
    }

    inline infix fun recovery(function: (Failure) -> JsResult<@UnsafeVariance T>): JsResult<T> = when (this) {
        is Success -> this
        is Failure -> function(this)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

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

fun <T> T.asSuccess(location: JsLocation): JsResult<T> = JsResult.Success(location, this)
fun <E : JsError> E.asFailure(location: JsLocation): JsResult<Nothing> = JsResult.Failure(location, this)
