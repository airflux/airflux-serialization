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

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.fold
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

            @JvmStatic
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

@OptIn(ExperimentalContracts::class)
public fun <T> ReadingResult<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is ReadingResult.Success<T>)
        returns(false) implies (this@isSuccess is ReadingResult.Failure)
    }
    return this is ReadingResult.Success<T>
}

@OptIn(ExperimentalContracts::class)
public fun <T> ReadingResult<T>.isError(): Boolean {
    contract {
        returns(false) implies (this@isError is ReadingResult.Success<T>)
        returns(true) implies (this@isError is ReadingResult.Failure)
    }
    return this is ReadingResult.Failure
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> ReadingResult<T>.map(transform: (T) -> R): ReadingResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return bind { location, value -> transform(value).toSuccess(location) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> ReadingResult<T>.bind(
    transform: (location: JsLocation, value: T) -> ReadingResult<R>
): ReadingResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(location, value) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> ReadingResult<T>.fold(
    ifFailure: (ReadingResult.Failure) -> R,
    ifSuccess: (ReadingResult.Success<T>) -> R
): R {
    contract {
        callsInPlace(ifFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(ifSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) ifSuccess(this) else ifFailure(this)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> ReadingResult<T>.recovery(
    function: (ReadingResult.Failure) -> ReadingResult<T>
): ReadingResult<T> {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else function(this)
}

public fun <T> ReadingResult<T>.getOrNull(): T? = if (isSuccess()) this.value else null

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> ReadingResult<T>.getOrElse(defaultValue: () -> T): T {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else defaultValue()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> ReadingResult<T>.getOrHandle(handler: (ReadingResult.Failure) -> T): T {
    contract {
        callsInPlace(handler, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else handler(this)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> ReadingResult<T>.orElse(defaultValue: () -> ReadingResult<T>): ReadingResult<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else defaultValue()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> ReadingResult<T>.orThrow(exceptionBuilder: (ReadingResult.Failure) -> Throwable): T {
    contract {
        callsInPlace(exceptionBuilder, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(this)
}

public fun <EB, O, T> ReadingResult<T>.filter(
    env: JsReaderEnv<EB, O>,
    context: JsContext,
    predicate: JsPredicate<EB, O, T & Any>
): ReadingResult<T?> {
    return if (isSuccess()) {
        if (value == null)
            this
        else {
            if (predicate.test(env, context, location, value))
                this
            else
                success(location = location, value = null)
        }
    } else
        this
}

public fun <EB, O, T> ReadingResult<T>.validation(
    env: JsReaderEnv<EB, O>,
    context: JsContext,
    validator: JsValidator<EB, O, T>
): ReadingResult<T> =
    if (isSuccess())
        validator.validate(env, context, location, value)
            .fold(
                ifInvalid = { it },
                ifValid = { this }
            )
    else
        this

@OptIn(ExperimentalContracts::class)
public inline fun <T> ReadingResult<T>.ifNullValue(defaultValue: () -> T): ReadingResult<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess() && value == null) defaultValue().toSuccess(location) else this
}

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
@OptIn(ExperimentalContracts::class)
public inline fun <EB, O, T> withCatching(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    block: () -> ReadingResult<T>
): ReadingResult<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return try {
        block()
    } catch (expected: Throwable) {
        val handler = env.exceptionsHandler ?: throw expected
        handler.handle(env, location, expected).toFailure(location)
    }
}
