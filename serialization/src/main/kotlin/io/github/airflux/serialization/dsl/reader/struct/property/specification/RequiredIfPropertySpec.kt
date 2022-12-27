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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.struct.readOptional
import io.github.airflux.serialization.core.reader.struct.readRequired

public fun <EB, CTX, T : Any> required(
    name: String,
    reader: Reader<EB, CTX, T>,
    predicate: (ReaderEnv<EB, CTX>, Location) -> Boolean
): StructPropertySpec.Nullable<EB, CTX, T>
    where EB : PathMissingErrorBuilder =
    required(PropertyPath(name), reader, predicate)

public fun <EB, CTX, T : Any> required(
    path: PropertyPath,
    reader: Reader<EB, CTX, T>,
    predicate: (ReaderEnv<EB, CTX>, Location) -> Boolean
): StructPropertySpec.Nullable<EB, CTX, T>
    where EB : PathMissingErrorBuilder =
    StructPropertySpec.Nullable(
        path = PropertyPaths(path),
        reader = { env, location, source ->
            val lookup = source.lookup(location, path)
            if (predicate(env, location))
                readRequired(env, lookup, reader)
            else
                readOptional(env, lookup, reader)
        }
    )