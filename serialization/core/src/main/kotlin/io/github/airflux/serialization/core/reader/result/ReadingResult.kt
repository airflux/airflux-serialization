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

@file:Suppress("TooManyFunctions")

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.core.reader.validation.fold

public sealed class ReadingResult<out T> {

    public companion object;

    public data class Success<T>(val location: JsLocation, val value: T) : ReadingResult<T>()

    public class Failure private constructor(public val causes: List<Cause>) : ReadingResult<Nothing>() {

        public constructor(location: JsLocation, error: Error) : this(listOf(Cause(location, error)))

        public constructor(location: JsLocation, errors: Errors) : this(listOf(Cause(location, errors)))

        public operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        override fun toString(): String = buildString {
            append("Failure(causes=")
            append(causes.toString())
            append(")")
        }

        public data class Cause(val location: JsLocation, val errors: Errors) {
            public constructor(location: JsLocation, error: Error) : this(location, Errors(error))
        }

        public companion object {
            public fun Collection<Failure>.merge(): Failure = Failure(causes = flatMap { failure -> failure.causes })
        }
    }

    public interface Error

    @JvmInline
    public value class Errors private constructor(public val items: List<Error>) {

        public constructor(error: Error) : this(listOf(error))

        public operator fun plus(other: Errors): Errors = Errors(items + other.items)
    }
}

public infix fun <T, R> ReadingResult<T>.map(transform: (T) -> R): ReadingResult<R> =
    flatMap { location, value -> transform(value).toSuccess(location) }

public infix fun <T, R> ReadingResult<T>.flatMap(transform: (JsLocation, T) -> ReadingResult<R>): ReadingResult<R> = fold(
    ifFailure = ::identity,
    ifSuccess = { transform(it.location, it.value) }
)

public inline fun <T, R> ReadingResult<T>.fold(
    ifFailure: (ReadingResult.Failure) -> R,
    ifSuccess: (ReadingResult.Success<T>) -> R
): R = when (this) {
    is ReadingResult.Success -> ifSuccess(this)
    is ReadingResult.Failure -> ifFailure(this)
}

public inline infix fun <T> ReadingResult<T>.recovery(
    function: (ReadingResult.Failure) -> ReadingResult<T>
): ReadingResult<T> = fold(
    ifFailure = { function(it) },
    ifSuccess = ::identity
)

public fun <T> ReadingResult<T>.getOrNull(): T? = fold(
    ifFailure = { null },
    ifSuccess = { it.value }
)

public infix fun <T> ReadingResult<T>.getOrElse(defaultValue: () -> T): T = fold(
    ifFailure = { defaultValue() },
    ifSuccess = { it.value }
)

public infix fun <T> ReadingResult<T>.getOrHandle(handler: (ReadingResult.Failure) -> T): T = fold(
    ifFailure = { handler(it) },
    ifSuccess = { it.value }
)

public infix fun <T> ReadingResult<T>.orElse(defaultValue: () -> ReadingResult<T>): ReadingResult<T> = fold(
    ifFailure = { defaultValue() },
    ifSuccess = ::identity
)

public infix fun <T> ReadingResult<T>.orThrow(exceptionBuilder: (ReadingResult.Failure) -> Throwable): T = fold(
    ifFailure = { throw exceptionBuilder(it) },
    ifSuccess = { it.value }
)

public fun <EB, O, CTX, T> ReadingResult<T>.filter(
    env: ReaderEnv<EB, O>,
    context: CTX,
    predicate: ReaderPredicate<EB, O, CTX, T & Any>
): ReadingResult<T?> = fold(
    ifFailure = ::identity,
    ifSuccess = { result ->
        if (result.value == null)
            result
        else {
            if (predicate.test(env, context, result.location, result.value))
                result
            else
                success(location = result.location, value = null)
        }
    }
)

public fun <EB, O, CTX, T> ReadingResult<T>.validation(
    env: ReaderEnv<EB, O>,
    context: CTX,
    validator: Validator<EB, O, CTX, T>
): ReadingResult<T> = fold(
    ifFailure = ::identity,
    ifSuccess = { result ->
        validator.validate(env, context, result.location, result.value)
            .fold(
                ifInvalid = { it },
                ifValid = { result }
            )
    }
)

public inline fun <T> ReadingResult<T>.ifNullValue(defaultValue: () -> T): ReadingResult<T> = fold(
    ifFailure = ::identity,
    ifSuccess = { result -> if (result.value != null) result else defaultValue().toSuccess(result.location) }
)

public fun <T> success(location: JsLocation, value: T): ReadingResult<T> =
    ReadingResult.Success(location = location, value = value)

public fun <E : ReadingResult.Error> failure(location: JsLocation, error: E): ReadingResult<Nothing> =
    ReadingResult.Failure(location = location, error = error)

public fun <T> T.toSuccess(location: JsLocation): ReadingResult<T> = success(location = location, value = this)

public fun <E : ReadingResult.Error> E.toFailure(location: JsLocation): ReadingResult<Nothing> =
    failure(location = location, error = this)

/**
 * Calls the specified function [block] and returns its result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution
 * and using the [io.github.airflux.serialization.core.reader.env.exception.ExceptionsHandler] from env to handle it.
 */
public inline fun <EB, O, T> withCatching(
    env: ReaderEnv<EB, O>,
    location: JsLocation,
    block: () -> ReadingResult<T>
): ReadingResult<T> =
    try {
        block()
    } catch (expected: Throwable) {
        val handler = env.exceptionsHandler ?: throw expected
        handler.handle(env, location, expected).toFailure(location)
    }
