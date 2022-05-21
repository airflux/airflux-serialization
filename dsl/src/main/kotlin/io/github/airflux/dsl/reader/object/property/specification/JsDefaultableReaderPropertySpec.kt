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

package io.github.airflux.dsl.reader.`object`.property.specification

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.or
import io.github.airflux.core.reader.readWithDefault
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

internal class JsDefaultableReaderPropertySpec<T : Any> private constructor(
    override val path: JsPaths,
    override val reader: JsReader<T>
) : JsReaderPropertySpec.Defaultable<T> {

    constructor(path: JsPath, reader: JsReader<T>, default: () -> T) : this(
        path = JsPaths(path),
        reader = { context, location, input ->
            val lookup = JsLookup.apply(location, path, input)
            readWithDefault(context, lookup, reader, default)
        }
    )

    override fun validation(validator: JsValidator<T>): JsReaderPropertySpec.Defaultable<T> =
        JsDefaultableReaderPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun or(alt: JsReaderPropertySpec.Defaultable<T>): JsReaderPropertySpec.Defaultable<T> =
        JsDefaultableReaderPropertySpec(path = path.append(alt.path), reader = reader or alt.reader)
}
