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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv

public sealed class ReaderResult<out T> {

    public companion object;

    public infix fun <R> map(transform: (T) -> R): ReaderResult<R> =
        flatMap { value -> transform(value).success() }

    public fun <R> flatMap(transform: (T) -> ReaderResult<R>): ReaderResult<R> = fold(
        ifFailure = ::identity,
        ifSuccess = { transform(it.value) }
    )

    public data class Success<T>(val value: T) : ReaderResult<T>()

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

    public class Errors private constructor(public val items: List<Error>) {

        public operator fun plus(other: Errors): Errors = Errors(items + other.items)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Errors && this.items == other.items)

        override fun hashCode(): Int = items.hashCode()

        override fun toString(): String = buildString {
            append("Errors(items=")
            append(items.toString())
            append(")")
        }

        public companion object {

            public operator fun invoke(error: Error, vararg errors: Error): Errors = if (errors.isEmpty())
                Errors(listOf(error))
            else
                Errors(listOf(error) + errors.asList())

            public operator fun invoke(errors: List<Error>): Errors? =
                errors.takeIf { it.isNotEmpty() }?.let { Errors(it) }
        }
    }
}

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

public fun <T> T.success(): ReaderResult<T> = ReaderResult.Success(this)
public fun <E : ReaderResult.Error> E.failure(location: Location): ReaderResult<Nothing> =
    ReaderResult.Failure(location, this)

/**
 * Calls the specified function [block] and returns its result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution
 * and using the [ExceptionsHandler] from env to handle it.
 */
public inline fun <EB, CTX, T> withCatching(
    env: ReaderEnv<EB, CTX>,
    location: Location,
    block: () -> ReaderResult<T>
): ReaderResult<T> =
    try {
        block()
    } catch (expected: Throwable) {
        env.exceptionsHandler
            ?.handle(env, location, expected)
            ?.failure(location)
            ?: throw expected
    }
