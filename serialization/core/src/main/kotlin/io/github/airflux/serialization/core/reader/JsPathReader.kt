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
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validation.JsValidator

public fun interface JsPathReader<EB, O, out T> : JsReader<EB, O, T> {

    public companion object {

        public fun <EB, O, T> optional(
            path: JsPath,
            reader: JsReader<EB, O, T>
        ): JsPathReader<EB, O, T?>
            where EB : InvalidTypeErrorBuilder =
            JsPathReader { env, location, source ->
                val lookup = source.lookup(location, path)
                readOptional(env, lookup, reader)
            }

        public fun <EB, O, T> optional(
            path: JsPath,
            reader: JsReader<EB, O, T>,
            default: (JsReaderEnv<EB, O>, JsLocation) -> T
        ): JsPathReader<EB, O, T>
            where EB : InvalidTypeErrorBuilder =
            JsPathReader { env, location, source ->
                val lookup = source.lookup(location, path)
                readOptional(env, lookup, reader, default)
            }

        public fun <EB, O, T> required(
            path: JsPath,
            reader: JsReader<EB, O, T>
        ): JsPathReader<EB, O, T>
            where EB : PathMissingErrorBuilder,
                  EB : InvalidTypeErrorBuilder =
            JsPathReader { env, location, source ->
                val lookup = source.lookup(location, path)
                readRequired(env, lookup, reader)
            }

        public fun <EB, O, T> required(
            path: JsPath,
            reader: JsReader<EB, O, T>,
            predicate: (JsReaderEnv<EB, O>, JsLocation) -> Boolean
        ): JsPathReader<EB, O, T?>
            where EB : PathMissingErrorBuilder,
                  EB : InvalidTypeErrorBuilder =
            JsPathReader { env, location, source ->
                val lookup = source.lookup(location, path)
                if (predicate(env, location.append(path)))
                    readRequired(env, lookup, reader)
                else
                    readOptional(env, lookup, reader)
            }
    }
}

public infix fun <EB, O, T> JsPathReader<EB, O, T>.or(alt: JsPathReader<EB, O, T>): JsPathReader<EB, O, T> =
    JsPathReader { env, location, source ->
        this@or.read(env, location, source)
            .recovery { failure ->
                alt.read(env, location, source)
                    .recovery { alternative -> failure + alternative }
            }
    }

public infix fun <EB, O, T> JsPathReader<EB, O, T>.filter(
    predicate: JsPredicate<EB, O, T & Any>
): JsPathReader<EB, O, T?> =
    JsPathReader { env, location, source ->
        this@filter.read(env, location, source)
            .filter(env, predicate)
    }

public infix fun <EB, O, T> JsPathReader<EB, O, T>.validation(
    validator: JsValidator<EB, O, T>
): JsPathReader<EB, O, T> =
    JsPathReader { env, location, source ->
        this@validation.read(env, location, source)
            .validation(env, validator)
    }
