/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.struct.property

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec

public class StructProperty<EB, O, CTX, T> private constructor(
    public val paths: JsPaths,
    private val reader: Reader<EB, O, CTX, T>
) {
    public constructor(spec: StructPropertySpec<EB, O, CTX, T>) : this(spec.paths, spec.reader)

    public fun read(env: ReaderEnv<EB, O>, context: CTX, location: JsLocation, source: ValueNode): ReadingResult<T> =
        reader.read(env, context, location, source)
}
