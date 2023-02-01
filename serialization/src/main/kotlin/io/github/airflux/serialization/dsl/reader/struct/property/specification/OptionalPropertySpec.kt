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

import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.struct.readOptional

public fun <EB, O, CTX, T : Any> optional(
    name: String,
    reader: Reader<EB, O, CTX, T>
): StructPropertySpec.Nullable<EB, O, CTX, T>
    where EB : InvalidTypeErrorBuilder =
    optional(PropertyPath(name), reader)

public fun <EB, O, CTX, T : Any> optional(
    path: PropertyPath,
    reader: Reader<EB, O, CTX, T>
): StructPropertySpec.Nullable<EB, O, CTX, T>
    where EB : InvalidTypeErrorBuilder =
    StructPropertySpec.Nullable(
        paths = PropertyPaths(path),
        reader = { env, context, location, source ->
            val lookup = source.lookup(location, path)
            readOptional(env, context, lookup, reader)
        }
    )
