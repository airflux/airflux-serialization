/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.writer.struct.property.specification

import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv

public fun <O, T : Any, P : Any> nonNullable(
    name: String,
    from: T.() -> P,
    writer: JsWriter<O, P>
): JsStructPropertySpec<O, T, P> =
    JsStructPropertySpec(name = name, from = JsStructPropertySpec.Extractor.WithoutEnv(from), writer = writer)

public fun <O, T : Any, P : Any> nonNullable(
    name: String,
    from: T.(JsWriterEnv<O>) -> P,
    writer: JsWriter<O, P>
): JsStructPropertySpec<O, T, P> =
    JsStructPropertySpec(name = name, from = JsStructPropertySpec.Extractor.WithEnv(from), writer = writer)
