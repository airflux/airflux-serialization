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

import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.struct.readWithDefault
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <EB, CTX, T : Any> defaultable(
    name: String,
    reader: Reader<EB, CTX, T>,
    default: (ReaderEnv<EB, CTX>) -> T
): ObjectPropertySpec.Defaultable<EB, CTX, T> =
    defaultable(PropertyPath(name), reader, default)

public fun <EB, CTX, T : Any> defaultable(
    path: PropertyPath,
    reader: Reader<EB, CTX, T>,
    default: (ReaderEnv<EB, CTX>) -> T
): ObjectPropertySpec.Defaultable<EB, CTX, T> =
    ObjectPropertySpec.Defaultable(
        path = PropertyPaths(path),
        reader = { env, location, source ->
            val lookup = source.lookup(location, path)
            readWithDefault(env, lookup, reader, default)
        }
    )

public fun <EB, CTX, T : Any> defaultable(
    paths: PropertyPaths,
    reader: Reader<EB, CTX, T>,
    default: (ReaderEnv<EB, CTX>) -> T
): ObjectPropertySpec.Defaultable<EB, CTX, T> =
    ObjectPropertySpec.Defaultable(
        path = paths,
        reader = { env, location, source ->
            val lookup: Lookup = paths.fold(
                initial = { path -> source.lookup(location, path) },
                operation = { lookup, path ->
                    if (lookup is Lookup.Defined) return@fold lookup
                    source.lookup(location, path)
                }
            )
            readWithDefault(env, lookup, reader, default)
        }
    )

public infix fun <EB, CTX, T : Any> ObjectPropertySpec.Defaultable<EB, CTX, T>.validation(
    validator: Validator<EB, CTX, T>
): ObjectPropertySpec.Defaultable<EB, CTX, T> =
    ObjectPropertySpec.Defaultable(
        path = path,
        reader = { env, location, source ->
            reader.read(env, location, source).validation(env, location, validator)
        }
    )

public infix fun <EB, CTX, T : Any> ObjectPropertySpec.Defaultable<EB, CTX, T>.or(
    alt: ObjectPropertySpec.Defaultable<EB, CTX, T>
): ObjectPropertySpec.Defaultable<EB, CTX, T> =
    ObjectPropertySpec.Defaultable(path = path.append(alt.path), reader = reader or alt.reader)
