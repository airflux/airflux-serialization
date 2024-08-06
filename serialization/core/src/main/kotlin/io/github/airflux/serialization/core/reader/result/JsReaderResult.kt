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

@file:Suppress("TooManyFunctions")

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.NonEmptyList
import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.common.plus
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.fold
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed class JsReaderResult<out T> {

    public companion object;

    public data class Success<T>(public val location: JsLocation, public val value: T) : JsReaderResult<T>()

    public data class Failure(public val causes: NonEmptyList<Cause>) : JsReaderResult<Nothing>() {

        public constructor(location: JsLocation, error: Error) : this(NonEmptyList(Cause(location, error)))

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        public data class Cause(val location: JsLocation, val errors: NonEmptyList<Error>) {
            public constructor(location: JsLocation, error: Error) : this(location, NonEmptyList(error))
        }
    }

    public interface Error
}

@OptIn(ExperimentalContracts::class)
public fun <T> JsReaderResult<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is JsReaderResult.Success<T>)
        returns(false) implies (this@isSuccess is JsReaderResult.Failure)
    }
    return this is JsReaderResult.Success<T>
}

@OptIn(ExperimentalContracts::class)
public fun <T> JsReaderResult<T>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is JsReaderResult.Success<T>)
        returns(true) implies (this@isFailure is JsReaderResult.Failure)
    }
    return this is JsReaderResult.Failure
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> JsReaderResult<T>.map(transform: (T) -> R): JsReaderResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return bind { transform(it.value).toSuccess(it.location) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> JsReaderResult<T>.bind(
    transform: (JsReaderResult.Success<T>) -> JsReaderResult<R>
): JsReaderResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(this) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> JsReaderResult<T>.fold(
    onFailure: (JsReaderResult.Failure) -> R,
    onSuccess: (JsReaderResult.Success<T>) -> R
): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) onSuccess(this) else onFailure(this)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> JsReaderResult<T>.recovery(
    function: (JsReaderResult.Failure) -> JsReaderResult<T>
): JsReaderResult<T> {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else function(this)
}

public fun <T> JsReaderResult<T>.getOrNull(): T? = if (isSuccess()) this.value else null

public infix fun <T> JsReaderResult<T>.getOrElse(default: T): T = if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> JsReaderResult<T>.getOrElse(handler: (JsReaderResult.Failure) -> T): T {
    contract {
        callsInPlace(handler, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else handler(this)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> JsReaderResult<T>.orElse(default: () -> JsReaderResult<T>): JsReaderResult<T> {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> JsReaderResult<T>.orThrow(exceptionBuilder: (JsReaderResult.Failure) -> Throwable): T {
    contract {
        callsInPlace(exceptionBuilder, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(this)
}

public fun <EB, O, T> JsReaderResult<T>.filter(
    env: JsReaderEnv<EB, O>,
    predicate: JsPredicate<EB, O, T & Any>
): JsReaderResult<T?> =
    if (isSuccess() && value != null && !predicate.test(env, location, value))
        success(location = location, value = null)
    else
        this

public fun <EB, O, T> JsReaderResult<T>.validation(
    env: JsReaderEnv<EB, O>,
    validator: JsValidator<EB, O, T>
): JsReaderResult<T> =
    if (isSuccess())
        validator.validate(env, location, value)
            .fold(onInvalid = ::identity, onValid = { this })
    else
        this

@OptIn(ExperimentalContracts::class)
public inline fun <T> JsReaderResult<T>.ifNullValue(defaultValue: () -> T): JsReaderResult<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess() && value == null) defaultValue().toSuccess(location) else this
}

public operator fun JsReaderResult.Failure?.plus(other: JsReaderResult.Failure): JsReaderResult.Failure =
    if (this != null)
        JsReaderResult.Failure(causes + other.causes)
    else
        other

public fun <T> success(location: JsLocation, value: T): JsReaderResult<T> =
    JsReaderResult.Success(location = location, value = value)

public fun <E : JsReaderResult.Error> failure(location: JsLocation, error: E): JsReaderResult<Nothing> =
    JsReaderResult.Failure(location = location, error = error)

public fun <T> T.toSuccess(location: JsLocation): JsReaderResult<T> = success(location = location, value = this)

public fun <E : JsReaderResult.Error> E.toFailure(location: JsLocation): JsReaderResult<Nothing> =
    failure(location = location, error = this)
