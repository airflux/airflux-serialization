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
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.struct.readOptional
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <EB, CTX, T : Any> optional(
    name: String,
    reader: Reader<EB, CTX, T>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    optional(PropertyPath(name), reader)

public fun <EB, CTX, T : Any> optional(
    path: PropertyPath,
    reader: Reader<EB, CTX, T>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    ObjectPropertySpec.Optional(
        path = PropertyPaths(path),
        reader = { env, location, source ->
            val lookup = source.lookup(location, path)
            readOptional(env, lookup, reader)
        }
    )

public fun <EB, CTX, T : Any> optional(
    paths: PropertyPaths,
    reader: Reader<EB, CTX, T>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    ObjectPropertySpec.Optional(
        path = paths,
        reader = { env, location, source ->
            val lookup: Lookup = paths.fold(
                initial = { path -> source.lookup(location, path) },
                operation = { lookup, path ->
                    if (lookup is Lookup.Defined) return@fold lookup
                    source.lookup(location, path)
                }
            )
            readOptional(env, lookup, reader)
        }
    )

public infix fun <EB, CTX, T : Any> ObjectPropertySpec.Optional<EB, CTX, T>.validation(
    validator: Validator<EB, CTX, T?>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    ObjectPropertySpec.Optional(
        path = path,
        reader = { env, location, source ->
            reader.read(env, location, source).validation(env, location, validator)
        }
    )

public infix fun <EB, CTX, T : Any> ObjectPropertySpec.Optional<EB, CTX, T>.filter(
    predicate: ReaderPredicate<EB, CTX, T>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    ObjectPropertySpec.Optional(
        path = path,
        reader = { env, location, source ->
            reader.read(env, location, source).filter(env, predicate)
        }
    )

public infix fun <EB, CTX, T : Any> ObjectPropertySpec.Optional<EB, CTX, T>.or(
    alt: ObjectPropertySpec.Optional<EB, CTX, T>
): ObjectPropertySpec.Optional<EB, CTX, T> =
    ObjectPropertySpec.Optional(path = path.append(alt.path), reader = reader or alt.reader)
