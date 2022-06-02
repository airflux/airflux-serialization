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
import io.github.airflux.core.reader.readOptional
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

internal class JsObjectOptionalWithDefaultPropertySpec<T : Any> private constructor(
    override val path: JsPaths,
    override val reader: JsReader<T>
) : JsObjectPropertySpec.OptionalWithDefault<T> {

    override fun validation(validator: JsValidator<T>): JsObjectPropertySpec.OptionalWithDefault<T> =
        JsObjectOptionalWithDefaultPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun or(alt: JsObjectPropertySpec.OptionalWithDefault<T>): JsObjectPropertySpec.OptionalWithDefault<T> =
        JsObjectOptionalWithDefaultPropertySpec(path = path.append(alt.path), reader = reader or alt.reader)

    companion object {

        fun <T : Any> of(
            path: JsPath,
            reader: JsReader<T>,
            default: () -> T
        ): JsObjectPropertySpec.OptionalWithDefault<T> =
            JsObjectOptionalWithDefaultPropertySpec(
                path = JsPaths(path),
                reader = buildReader(path, reader, default)
            )

        fun <T : Any> of(
            paths: JsPaths,
            reader: JsReader<T>,
            default: () -> T
        ): JsObjectPropertySpec.OptionalWithDefault<T> =
            JsObjectOptionalWithDefaultPropertySpec(
                path = paths,
                reader = paths.items
                    .map { path -> buildReader(path, reader, default) }
                    .reduce { acc, element -> acc.or(element) }
            )

        private fun <T : Any> buildReader(path: JsPath, reader: JsReader<T>, default: () -> T) =
            JsReader { context, location, input ->
                val lookup = JsLookup.apply(location, path, input)
                readOptional(context, lookup, reader, default)
            }
    }
}
