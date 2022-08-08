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

package io.github.airflux.serialization.dsl.reader.struct.builder.property.specification

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.struct.readRequired
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T : Any> required(name: String, reader: Reader<T>): ObjectPropertySpec.Required<T> =
    required(PropertyPath(name), reader)

public fun <T : Any> required(path: PropertyPath, reader: Reader<T>): ObjectPropertySpec.Required<T> =
    ObjectPropertySpec.Required(
        path = PropertyPaths(path),
        reader = { context, location, source ->
            val lookup = source.lookup(location, path)
            readRequired(context, lookup, reader)
        }
    )

public fun <T : Any> required(paths: PropertyPaths, reader: Reader<T>): ObjectPropertySpec.Required<T> =
    ObjectPropertySpec.Required(
        path = paths,
        reader = Reader { context, location, source ->
            val errorBuilder = context[PathMissingErrorBuilder]
            val failures = paths.items
                .map { path ->
                    val lookup = source.lookup(location, path)
                    if (lookup is Lookup.Defined) return@Reader readRequired(context, lookup, reader)
                    ReaderResult.Failure(location = location.append(path), error = errorBuilder.build())
                }
            failures.merge()
        }
    )

public infix fun <T : Any> ObjectPropertySpec.Required<T>.validation(
    validator: Validator<T>
): ObjectPropertySpec.Required<T> =
    ObjectPropertySpec.Required(
        path = path,
        reader = { context, location, source ->
            reader.read(context, location, source).validation(context, validator)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.Required<T>.or(
    alt: ObjectPropertySpec.Required<T>
): ObjectPropertySpec.Required<T> =
    ObjectPropertySpec.Required(path = path.append(alt.path), reader = reader or alt.reader)
