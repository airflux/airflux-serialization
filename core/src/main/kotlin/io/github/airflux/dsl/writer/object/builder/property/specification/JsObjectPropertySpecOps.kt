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

package io.github.airflux.dsl.writer.`object`.builder.property.specification

import io.github.airflux.core.writer.JsWriter

public fun <T : Any, P : Any> required(
    name: String,
    from: (T) -> P,
    writer: JsWriter<P>
): JsObjectPropertySpec.Required<T, P> = JsObjectRequiredPropertySpec(name, from, writer)

public fun <T : Any, P : Any> optional(
    name: String,
    from: (T) -> P?,
    writer: JsWriter<P>
): JsObjectPropertySpec.Optional<T, P> = JsObjectOptionalPropertySpec.of(name, from, writer)

public fun <T : Any, P : Any> nullable(
    name: String,
    from: (T) -> P?,
    writer: JsWriter<P>
): JsObjectPropertySpec.Nullable<T, P> = JsObjectNullablePropertySpec.of(name, from, writer)
