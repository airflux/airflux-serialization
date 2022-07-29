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

package io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.`object`.readNullable
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.JsValidator

public fun <T : Any> nullable(name: String, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    nullable(JsPath(name), reader)

public fun <T : Any> nullable(path: JsPath, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    JsObjectPropertySpec.Nullable(
        path = JsPaths(path),
        reader = { context, location, input ->
            val lookup = input.lookup(location, path)
            readNullable(context, lookup, reader)
        }
    )

public fun <T : Any> nullable(paths: JsPaths, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    JsObjectPropertySpec.Nullable(
        path = paths,
        reader = JsReader { context, location, input ->
            val errorBuilder = context[PathMissingErrorBuilder]
            val failures = paths.items
                .map { path ->
                    val lookup = input.lookup(location, path)
                    if (lookup is JsLookup.Defined) return@JsReader readNullable(context, lookup, reader)
                    JsResult.Failure(location = location.append(path), error = errorBuilder.build())
                }
            failures.merge()
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.Nullable<T>.validation(
    validator: JsValidator<T?>
): JsObjectPropertySpec.Nullable<T> =
    JsObjectPropertySpec.Nullable(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).validation(context, validator)
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.Nullable<T>.filter(
    predicate: JsPredicate<T>
): JsObjectPropertySpec.Nullable<T> =
    JsObjectPropertySpec.Nullable(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).filter(context, predicate)
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.Nullable<T>.or(
    alt: JsObjectPropertySpec.Nullable<T>
): JsObjectPropertySpec.Nullable<T> =
    JsObjectPropertySpec.Nullable(path = path.append(alt.path), reader = reader or alt.reader)
