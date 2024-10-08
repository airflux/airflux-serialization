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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.map
import io.github.airflux.serialization.core.reader.result.orElse
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsValue

public fun interface JsReader<EB, O, out T> {

    /**
     * Convert the [JsValue] into a T
     */
    public fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue): JsReaderResult<T>
}

/**
 * Create a new [JsReader] which maps the value produced by this [JsReader].
 *
 * @param[R] The type of the value produced by the new [JsReader].
 * @param transform the function applied on the result of the current instance,
 * if successful
 * @return A new [JsReader] with the updated behavior.
 */
public infix fun <EB, O, T, R> JsReader<EB, O, T>.map(transform: (T) -> R): JsReader<EB, O, R> =
    JsReader { env, location, source ->
        this@map.read(env, location, source)
            .map(transform)
    }

public infix fun <EB, O, T, R> JsReader<EB, O, T>.bind(
    transform: (JsReaderEnv<EB, O>, JsReaderResult.Success<T>) -> JsReaderResult<R>
): JsReader<EB, O, R> =
    JsReader { env, location, source ->
        this@bind.read(env, location, source)
            .fold(
                onFailure = ::identity,
                onSuccess = { transform(env, it) }
            )
    }

/**
 * Combines the current [JsReader] with the provided `alt` reader.
 * If the current reader fails to parse the JSON value, the `alt` reader will be used to attempt the parsing.
 * The result of the first successful parsing will be returned or the last error.
 *
 * @param alt the alternative [JsReader] to use if the current reader fails
 * @return a new [JsReader] that combines the current reader with the alternative reader
 */
public infix fun <EB, O, T> JsReader<EB, O, T>.orElse(alt: JsReader<EB, O, T>): JsReader<EB, O, T> =
    JsReader { env, location, source ->
        this@orElse.read(env, location, source)
            .orElse { alt.read(env, location, source) }
    }

/**
 * Creates a new [JsReader], based on this one, which first executes this
 * [JsReader] logic then, if this [JsReader] resulted in a [JsReaderResult.Error], runs
 * the other [JsReader] on the [JsValue].
 *
 * @param alt the [JsReader] to run if this one gets a [JsReaderResult.Error]
 * @return A new [JsReader] with the updated behavior.
 */
public infix fun <EB, O, T> JsReader<EB, O, T>.or(alt: JsReader<EB, O, T>): JsReader<EB, O, T> =
    JsReader { env, location, source ->
        this@or.read(env, location, source)
            .recovery { failure ->
                alt.read(env, location, source)
                    .recovery { alternative -> failure + alternative }
            }
    }

public infix fun <EB, O, T> JsReader<EB, O, T>.filter(
    predicate: JsPredicate<EB, O, T & Any>
): JsReader<EB, O, T?> =
    JsReader { env, location, source ->
        this@filter.read(env, location, source)
            .filter(env, predicate)
    }

public infix fun <EB, O, T> JsReader<EB, O, T>.validation(
    validator: JsValidator<EB, O, T>
): JsReader<EB, O, T> =
    JsReader { env, location, source ->
        this@validation.read(env, location, source)
            .validation(env, validator)
    }

public infix fun <EB, O, T> JsReader<EB, O, T>.ifNullValue(
    alternativeValue: (env: JsReaderEnv<EB, O>, location: JsLocation) -> T
): JsReader<EB, O, T> = JsReader { env, location, source ->
    if (source is JsNull)
        success(location = location, value = alternativeValue(env, location))
    else
        this@ifNullValue.read(env, location, source)
}

public fun <EB, O, T> JsReader<EB, O, T>.nullable(): JsReader<EB, O, T?> {
    return JsReader { env, location, source ->
        if (source is JsNull)
            success(location = location, value = null)
        else
            this@nullable.read(env, location, source)
    }
}
