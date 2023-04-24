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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.core.reader.validation.fold

public sealed class ReaderResult<out T> {

    public companion object;

    public data class Success<T>(val location: Location, val value: T) : ReaderResult<T>()

    public class Failure private constructor(public val causes: List<Cause>) : ReaderResult<Nothing>() {

        public constructor(location: Location, error: Error) : this(listOf(Cause(location, error)))

        public constructor(location: Location, errors: Errors) : this(listOf(Cause(location, errors)))

        public operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        override fun toString(): String = buildString {
            append("Failure(causes=")
            append(causes.toString())
            append(")")
        }

        public data class Cause(val location: Location, val errors: Errors) {
            public constructor(location: Location, error: Error) : this(location, Errors(error))
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

public infix fun <T, R> ReaderResult<T>.map(transform: (T) -> R): ReaderResult<R> =
    flatMap { location, value -> transform(value).success(location) }

public infix fun <T, R> ReaderResult<T>.flatMap(transform: (Location, T) -> ReaderResult<R>): ReaderResult<R> = fold(
    ifFailure = ::identity,
    ifSuccess = { transform(it.location, it.value) }
)

public inline fun <T, R> ReaderResult<T>.fold(
    ifFailure: (ReaderResult.Failure) -> R,
    ifSuccess: (ReaderResult.Success<T>) -> R
): R = when (this) {
    is ReaderResult.Success -> ifSuccess(this)
    is ReaderResult.Failure -> ifFailure(this)
}

public inline infix fun <T> ReaderResult<T>.recovery(
    function: (ReaderResult.Failure) -> ReaderResult<T>
): ReaderResult<T> = fold(
    ifFailure = { function(it) },
    ifSuccess = ::identity
)

public fun <T> ReaderResult<T>.getOrNull(): T? = fold(
    ifFailure = { null },
    ifSuccess = { it.value }
)

public infix fun <T> ReaderResult<T>.getOrElse(defaultValue: () -> T): T = fold(
    ifFailure = { defaultValue() },
    ifSuccess = { it.value }
)

public infix fun <T> ReaderResult<T>.getOrHandle(handler: (ReaderResult.Failure) -> T): T = fold(
    ifFailure = { handler(it) },
    ifSuccess = { it.value }
)

public infix fun <T> ReaderResult<T>.orElse(defaultValue: () -> ReaderResult<T>): ReaderResult<T> = fold(
    ifFailure = { defaultValue() },
    ifSuccess = ::identity
)

public infix fun <T> ReaderResult<T>.orThrow(exceptionBuilder: (ReaderResult.Failure) -> Throwable): T = fold(
    ifFailure = { throw exceptionBuilder(it) },
    ifSuccess = { it.value }
)

public fun <EB, O, CTX, T> ReaderResult<T>.filter(
    env: ReaderEnv<EB, O>,
    context: CTX,
    predicate: ReaderPredicate<EB, O, CTX, T & Any>
): ReaderResult<T?> = fold(
    ifFailure = ::identity,
    ifSuccess = { result ->
        if (result.value == null)
            result
        else {
            if (predicate.test(env, context, result.location, result.value))
                result
            else
                ReaderResult.Success(location = result.location, value = null)
        }
    }
)

public fun <EB, O, CTX, T> ReaderResult<T>.validation(
    env: ReaderEnv<EB, O>,
    context: CTX,
    validator: Validator<EB, O, CTX, T>
): ReaderResult<T> = fold(
    ifFailure = ::identity,
    ifSuccess = { result ->
        validator.validate(env, context, result.location, result.value)
            .fold(
                ifInvalid = { it },
                ifValid = { result }
            )
    }
)

public inline fun <T> ReaderResult<T>.ifNullValue(defaultValue: (Location) -> T & Any): ReaderResult<T & Any> = fold(
    ifFailure = ::identity,
    ifSuccess = { result ->
        if (result.value != null)
            @Suppress("UNCHECKED_CAST")
            result as ReaderResult.Success<T & Any>
        else
            defaultValue(result.location).success(result.location)
    }
)

public fun <T> T.success(location: Location): ReaderResult<T> = ReaderResult.Success(location = location, value = this)
public fun <E : ReaderResult.Error> E.failure(location: Location): ReaderResult<Nothing> =
    ReaderResult.Failure(location, this)

/**
 * Calls the specified function [block] and returns its result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution
 * and using the [io.github.airflux.serialization.core.reader.env.exception.ExceptionsHandler] from env to handle it.
 */
public inline fun <EB, O, T> withCatching(
    env: ReaderEnv<EB, O>,
    location: Location,
    block: () -> ReaderResult<T>
): ReaderResult<T> =
    try {
        block()
    } catch (expected: Throwable) {
        val handler = env.exceptionsHandler ?: throw expected
        handler.handle(env, location, expected).failure(location)
    }
