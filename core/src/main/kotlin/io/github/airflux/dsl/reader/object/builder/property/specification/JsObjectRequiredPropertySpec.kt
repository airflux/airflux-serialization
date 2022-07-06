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

package io.github.airflux.dsl.reader.`object`.builder.property.specification

import io.github.airflux.core.context.error.get
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.JsPaths
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.`object`.readRequired
import io.github.airflux.core.reader.or
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.validate
import io.github.airflux.core.reader.validator.JsValidator

public fun <T : Any> required(name: String, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    required(JsPath(name), reader)

public fun <T : Any> required(path: JsPath, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    JsObjectPropertySpec.Required(
        path = JsPaths(path),
        reader = { context, location, input ->
            val lookup = JsLookup.apply(location, path, input)
            readRequired(context, lookup, reader)
        }
    )

public fun <T : Any> required(paths: JsPaths, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    JsObjectPropertySpec.Required(
        path = paths,
        reader = JsReader { context, location, input ->
            val errorBuilder = context[PathMissingErrorBuilder]
            val failures = paths.items
                .map { path ->
                    val lookup = JsLookup.apply(location, path, input)
                    if (lookup is JsLookup.Defined) return@JsReader readRequired(context, lookup, reader)
                    JsResult.Failure(location = location.append(path), error = errorBuilder.build())
                }
            failures.merge()
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.Required<T>.validate(
    validator: JsValidator<T>
): JsObjectPropertySpec.Required<T> =
    JsObjectPropertySpec.Required(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).validate(context, validator)
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.Required<T>.or(
    alt: JsObjectPropertySpec.Required<T>
): JsObjectPropertySpec.Required<T> =
    JsObjectPropertySpec.Required(path = path.append(alt.path), reader = reader or alt.reader)
