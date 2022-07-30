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

import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.`object`.readNullable
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T : Any> nullableWithDefault(
    name: String,
    reader: Reader<T>,
    default: () -> T
): ObjectPropertySpec.NullableWithDefault<T> =
    nullableWithDefault(JsPath(name), reader, default)

public fun <T : Any> nullableWithDefault(
    path: JsPath,
    reader: Reader<T>,
    default: () -> T
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = JsPaths(path),
        reader = { context, location, input ->
            val lookup = input.lookup(location, path)
            readNullable(context, lookup, reader, default)
        }
    )

public fun <T : Any> nullableWithDefault(
    paths: JsPaths,
    reader: Reader<T>,
    default: () -> T
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = paths,
        reader = { context, location, input ->
            val lookup: Lookup = paths.fold(
                initial = { path -> input.lookup(location, path) },
                operation = { lookup, path ->
                    if (lookup is Lookup.Defined) return@fold lookup
                    input.lookup(location, path)
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
        reader = { context, location, input ->
            reader.read(context, location, input).validation(context, validator)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.NullableWithDefault<T>.filter(
    predicate: JsPredicate<T>
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).filter(context, predicate)
        }
    )

public infix fun <T : Any> ObjectPropertySpec.NullableWithDefault<T>.or(
    alt: ObjectPropertySpec.NullableWithDefault<T>
): ObjectPropertySpec.NullableWithDefault<T> =
    ObjectPropertySpec.NullableWithDefault(path = path.append(alt.path), reader = reader or alt.reader)
