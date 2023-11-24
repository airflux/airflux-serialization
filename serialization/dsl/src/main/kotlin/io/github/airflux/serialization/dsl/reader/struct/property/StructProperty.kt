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

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsPathReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec

public class StructProperty<EB, O, T> private constructor(
    public val paths: JsPaths,
    private val reader: JsPathReader<EB, O, T>
) {
    public constructor(spec: StructPropertySpec<EB, O, T>) : this(spec.paths, spec.reader)

    public fun read(
        env: JsReaderEnv<EB, O>,
        context: JsContext,
        location: JsLocation,
        source: JsValue
    ): JsReaderResult<T> =
        reader.read(env, context, location, source)
}
