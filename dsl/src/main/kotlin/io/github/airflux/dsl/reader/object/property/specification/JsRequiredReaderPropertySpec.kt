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
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.readRequired
import io.github.airflux.core.reader.validator.JsPropertyValidator
import io.github.airflux.core.reader.validator.extension.validation

internal class JsRequiredReaderPropertySpec<T : Any> private constructor(
    override val path: List<JsPath>,
    override val reader: JsReader<T>
) : JsReaderPropertySpec.Required<T> {

    constructor(
        path: JsPath,
        reader: JsReader<T>,
        pathMissingErrorBuilder: PathMissingErrorBuilder,
        invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : this(
        path = listOf(path),
        reader = { context, location, input ->
            val lookup = JsLookup.apply(location, path, input)
            readRequired(context, lookup, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }
    )

    override fun validation(validator: JsPropertyValidator<T>): JsReaderPropertySpec.Required<T> =
        JsRequiredReaderPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun or(alt: JsReaderPropertySpec.Required<T>): JsReaderPropertySpec.Required<T> =
        JsRequiredReaderPropertySpec(
            path = path + alt.path,
            reader = reader or alt.reader
        )
}
