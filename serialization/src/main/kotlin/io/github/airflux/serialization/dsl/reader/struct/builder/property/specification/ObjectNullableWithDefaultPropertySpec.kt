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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.struct.readNullable
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T : Any> nullableWithDefault(
    name: String,
    reader: Reader<T>,
    default: (ReaderContext, Location) -> T?
): ObjectPropertySpec.NullableWithDefault<T> =
    nullableWithDefault(PropertyPath(name), reader, default)

public fun <T : Any> nullableWithDefault(
    path: PropertyPath,
    reader: Reader<T>,
    default: (ReaderContext, Location) -> T?
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = PropertyPaths(path),
        reader = { context, location, source ->
            val lookup = source.lookup(location, path)
            readNullable(context, lookup, reader, default)
        }
    )

public fun <T : Any> nullableWithDefault(
    paths: PropertyPaths,
    reader: Reader<T>,
    default: (ReaderContext, Location) -> T?
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = paths,
        reader = { context, location, source ->
            val lookup: Lookup = paths.fold(
                initial = { path -> source.lookup(location, path) },
                operation = { lookup, path ->
                    if (lookup is Lookup.Defined) return@fold lookup
                    source.lookup(location, path)
                }
            )
            readNullable(context, lookup, reader, default)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.NullableWithDefault<T>.validation(
    validator: Validator<T?>
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = path,
        reader = { context, location, source ->
            reader.read(context, location, source).validation(context, validator)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.NullableWithDefault<T>.filter(
    predicate: ReaderPredicate<T>
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = path,
        reader = { context, location, source ->
            reader.read(context, location, source).filter(context, predicate)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.NullableWithDefault<T>.or(
    alt: ObjectPropertySpec.NullableWithDefault<T>
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(path = path.append(alt.path), reader = reader or alt.reader)
