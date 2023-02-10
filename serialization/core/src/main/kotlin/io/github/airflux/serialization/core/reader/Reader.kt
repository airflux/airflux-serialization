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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.map
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.core.value.ValueNode

public fun interface Reader<EB, O, CTX, out T> {

    /**
     * Convert the [ValueNode] into a T
     */
    public fun read(env: ReaderEnv<EB, O>, context: CTX, location: Location, source: ValueNode): ReaderResult<T>

    /**
     * Create a new [Reader] which maps the value produced by this [Reader].
     *
     * @param[R] The type of the value produced by the new [Reader].
     * @param transform the function applied on the result of the current instance,
     * if successful
     * @return A new [Reader] with the updated behavior.
     */
    public infix fun <R> map(transform: (T) -> R): Reader<EB, O, CTX, R> =
        Reader { env, context, location, source -> read(env, context, location, source).map(transform) }
}

public infix fun <EB, O, CTX, T, R> Reader<EB, O, CTX, T>.flatMapResult(
    transform: (ReaderEnv<EB, O>, CTX, Location, T) -> ReaderResult<R>
): Reader<EB, O, CTX, R> =
    Reader { env, context, location, source ->
        read(env, context, location, source)
            .fold(
                ifFailure = ::identity,
                ifSuccess = { transform(env, context, location, it.value) }
            )
    }

/**
 * Creates a new [Reader], based on this one, which first executes this
 * [Reader] logic then, if this [Reader] resulted in a [ReaderResult.Error], runs
 * the other [Reader] on the [ValueNode].
 *
 * @param alt the [Reader] to run if this one gets a [ReaderResult.Error]
 * @return A new [Reader] with the updated behavior.
 */
public infix fun <EB, O, CTX, T> Reader<EB, O, CTX, T>.or(alt: Reader<EB, O, CTX, T>): Reader<EB, O, CTX, T> =
    Reader { env, context, location, source ->
        read(env, context, location, source)
            .recovery { failure ->
                alt.read(env, context, location, source)
                    .recovery { alternative -> failure + alternative }
            }
    }

public infix fun <EB, O, CTX, T> Reader<EB, O, CTX, T?>.filter(
    predicate: ReaderPredicate<EB, O, CTX, T>
): Reader<EB, O, CTX, T?> =
    Reader { env, context, location, source ->
        this@filter.read(env, context, location, source)
            .filter(env, context, predicate)
    }

public infix fun <EB, O, CTX, T> Reader<EB, O, CTX, T>.validation(
    validator: Validator<EB, O, CTX, T>
): Reader<EB, O, CTX, T> =
    Reader { env, context, location, source ->
        this@validation.read(env, context, location, source)
            .validation(env, context, validator)
    }
