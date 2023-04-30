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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.ifNullValue
import io.github.airflux.serialization.core.reader.result.map
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsValue

public fun interface JsReader<EB, O, CTX, out T> {

    /**
     * Convert the [JsValue] into a T
     */
    public fun read(env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation, source: JsValue): ReadingResult<T>
}

/**
 * Create a new [JsReader] which maps the value produced by this [JsReader].
 *
 * @param[R] The type of the value produced by the new [JsReader].
 * @param transform the function applied on the result of the current instance,
 * if successful
 * @return A new [JsReader] with the updated behavior.
 */
public infix fun <EB, O, CTX, T, R> JsReader<EB, O, CTX, T>.map(transform: (T) -> R): JsReader<EB, O, CTX, R> =
    JsReader { env, context, location, source ->
        this@map.read(env, context, location, source)
            .map(transform)
    }

public infix fun <EB, O, CTX, T, R> JsReader<EB, O, CTX, T>.flatMapResult(
    transform: (JsReaderEnv<EB, O>, CTX, JsLocation, T) -> ReadingResult<R>
): JsReader<EB, O, CTX, R> =
    JsReader { env, context, location, source ->
        this@flatMapResult.read(env, context, location, source)
            .fold(
                ifFailure = ::identity,
                ifSuccess = { transform(env, context, location, it.value) }
            )
    }

/**
 * Creates a new [JsReader], based on this one, which first executes this
 * [JsReader] logic then, if this [JsReader] resulted in a [ReadingResult.Error], runs
 * the other [JsReader] on the [JsValue].
 *
 * @param alt the [JsReader] to run if this one gets a [ReadingResult.Error]
 * @return A new [JsReader] with the updated behavior.
 */
public infix fun <EB, O, CTX, T> JsReader<EB, O, CTX, T>.or(alt: JsReader<EB, O, CTX, T>): JsReader<EB, O, CTX, T> =
    JsReader { env, context, location, source ->
        this@or.read(env, context, location, source)
            .recovery { failure ->
                alt.read(env, context, location, source)
                    .recovery { alternative -> failure + alternative }
            }
    }

public infix fun <EB, O, CTX, T> JsReader<EB, O, CTX, T>.filter(
    predicate: JsPredicate<EB, O, CTX, T & Any>
): JsReader<EB, O, CTX, T?> =
    JsReader { env, context, location, source ->
        this@filter.read(env, context, location, source)
            .filter(env, context, predicate)
    }

public infix fun <EB, O, CTX, T> JsReader<EB, O, CTX, T>.validation(
    validator: JsValidator<EB, O, CTX, T>
): JsReader<EB, O, CTX, T> =
    JsReader { env, context, location, source ->
        this@validation.read(env, context, location, source)
            .validation(env, context, validator)
    }

public infix fun <EB, O, CTX, T> JsReader<EB, O, CTX, T>.ifNullValue(
    defaultValue: (env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation) -> T
): JsReader<EB, O, CTX, T> = JsReader { env, context, location, source ->
    this@ifNullValue.read(env, context, location, source)
        .ifNullValue { defaultValue(env, context, location) }
}

public fun <EB, O, CTX, T> JsReader<EB, O, CTX, T>.nullable(): JsReader<EB, O, CTX, T?> {
    return JsReader { env, context, location, source ->
        if (source is JsNull)
            success(location = location, value = null)
        else
            this@nullable.read(env, context, location, source)
    }
}
