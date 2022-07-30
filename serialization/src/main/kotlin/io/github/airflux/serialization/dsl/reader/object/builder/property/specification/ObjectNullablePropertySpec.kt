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
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.`object`.readNullable
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T : Any> nullable(name: String, reader: Reader<T>): ObjectPropertySpec.Nullable<T> =
    nullable(PropertyPath(name), reader)

public fun <T : Any> nullable(path: PropertyPath, reader: Reader<T>): ObjectPropertySpec.Nullable<T> =
    ObjectPropertySpec.Nullable(
        path = PropertyPaths(path),
        reader = { context, location, input ->
            val lookup = input.lookup(location, path)
            readNullable(context, lookup, reader)
        }
    )

public fun <T : Any> nullable(paths: PropertyPaths, reader: Reader<T>): ObjectPropertySpec.Nullable<T> =
    ObjectPropertySpec.Nullable(
        path = paths,
        reader = Reader { context, location, input ->
            val errorBuilder = context[PathMissingErrorBuilder]
            val failures = paths.items
                .map { path ->
                    val lookup = input.lookup(location, path)
                    if (lookup is Lookup.Defined) return@Reader readNullable(context, lookup, reader)
                    ReaderResult.Failure(location = location.append(path), error = errorBuilder.build())
                }
            failures.merge()
        }
    )

public infix fun <T : Any> ObjectPropertySpec.Nullable<T>.validation(
    validator: Validator<T?>
): ObjectPropertySpec.Nullable<T> =
    ObjectPropertySpec.Nullable(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).validation(context, validator)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.Nullable<T>.filter(
    predicate: ReaderPredicate<T>
): ObjectPropertySpec.Nullable<T> =
    ObjectPropertySpec.Nullable(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).filter(context, predicate)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.Nullable<T>.or(
    alt: ObjectPropertySpec.Nullable<T>
): ObjectPropertySpec.Nullable<T> =
    ObjectPropertySpec.Nullable(path = path.append(alt.path), reader = reader or alt.reader)
