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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.filter
import io.github.airflux.serialization.core.reader.ifNullValue
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.core.reader.validation.JsValidator

public class StructPropertySpec<EB, O, CTX, out T>(
    public val paths: JsPaths,
    public val reader: JsReader<EB, O, CTX, T>
)

public infix fun <EB, O, CTX, T> StructPropertySpec<EB, O, CTX, T>.validation(
    validator: JsValidator<EB, O, CTX, T>
): StructPropertySpec<EB, O, CTX, T> =
    StructPropertySpec(paths = paths, reader = reader.validation(validator))

public infix fun <EB, O, CTX, T> StructPropertySpec<EB, O, CTX, T>.or(
    alt: StructPropertySpec<EB, O, CTX, T>
): StructPropertySpec<EB, O, CTX, T> =
    StructPropertySpec(paths = paths.append(alt.paths), reader = reader or alt.reader)

public infix fun <EB, O, CTX, T> StructPropertySpec<EB, O, CTX, T>.filter(
    predicate: JsPredicate<EB, O, CTX, T & Any>
): StructPropertySpec<EB, O, CTX, T?> =
    StructPropertySpec(paths = paths, reader = reader.filter(predicate))

public infix fun <EB, O, CTX, T> StructPropertySpec<EB, O, CTX, T>.ifNullValue(
    defaultValue: (env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation) -> T
): StructPropertySpec<EB, O, CTX, T> =
    StructPropertySpec(paths = paths, reader = reader.ifNullValue(defaultValue))
