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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.struct.readOptional
import io.github.airflux.serialization.core.reader.struct.readRequired
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.value.JsValue

public fun interface JsPathReader<EB, O, CTX, out T> {

    public fun read(env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation, source: JsValue): ReadingResult<T>

    public companion object {

        public fun <EB, O, CTX, T> optional(
            path: JsPath,
            reader: JsReader<EB, O, CTX, T>
        ): JsPathReader<EB, O, CTX, T?>
            where EB : InvalidTypeErrorBuilder =
            JsPathReader { env, context, location, source ->
                val lookup = source.lookup(location, path)
                readOptional(env, context, lookup, reader)
            }

        public fun <EB, O, CTX, T> optional(
            path: JsPath,
            reader: JsReader<EB, O, CTX, T>,
            default: (JsReaderEnv<EB, O>, CTX) -> T
        ): JsPathReader<EB, O, CTX, T>
            where EB : InvalidTypeErrorBuilder =
            JsPathReader { env, context, location, source ->
                val lookup = source.lookup(location, path)
                readOptional(env, context, lookup, reader, default)
            }

        public fun <EB, O, CTX, T> required(
            path: JsPath,
            reader: JsReader<EB, O, CTX, T>
        ): JsPathReader<EB, O, CTX, T>
            where EB : PathMissingErrorBuilder,
                  EB : InvalidTypeErrorBuilder =
            JsPathReader { env, context, location, source ->
                val lookup = source.lookup(location, path)
                readRequired(env, context, lookup, reader)
            }

        public fun <EB, O, CTX, T> required(
            path: JsPath,
            reader: JsReader<EB, O, CTX, T>,
            predicate: (JsReaderEnv<EB, O>, CTX, JsLocation) -> Boolean
        ): JsPathReader<EB, O, CTX, T?>
            where EB : PathMissingErrorBuilder,
                  EB : InvalidTypeErrorBuilder =
            JsPathReader { env, context, location, source ->
                val lookup = source.lookup(location, path)
                if (predicate(env, context, location.append(path)))
                    readRequired(env, context, lookup, reader)
                else
                    readOptional(env, context, lookup, reader)
            }
    }
}

public infix fun <EB, O, CTX, T> JsPathReader<EB, O, CTX, T>.or(alt: JsPathReader<EB, O, CTX, T>): JsPathReader<EB, O, CTX, T> =
    JsPathReader { env, context, location, source ->
        this@or.read(env, context, location, source)
            .recovery { failure ->
                alt.read(env, context, location, source)
                    .recovery { alternative -> failure + alternative }
            }
    }

public infix fun <EB, O, CTX, T> JsPathReader<EB, O, CTX, T>.filter(
    predicate: JsPredicate<EB, O, CTX, T & Any>
): JsPathReader<EB, O, CTX, T?> =
    JsPathReader { env, context, location, source ->
        this@filter.read(env, context, location, source)
            .filter(env, context, predicate)
    }

public infix fun <EB, O, CTX, T> JsPathReader<EB, O, CTX, T>.validation(
    validator: JsValidator<EB, O, CTX, T>
): JsPathReader<EB, O, CTX, T> =
    JsPathReader { env, context, location, source ->
        this@validation.read(env, context, location, source)
            .validation(env, context, validator)
    }