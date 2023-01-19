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

package io.github.airflux.serialization.dsl.writer.struct.property.specification

import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.filter
import io.github.airflux.serialization.core.writer.predicate.WriterPredicate

public fun <O, CTX, T : Any, P : Any> nonNullable(
    name: String,
    from: (T) -> P,
    writer: Writer<O, CTX, P>
): StructPropertySpec.NonNullable<O, CTX, T, P> =
    StructPropertySpec.NonNullable(name = name, from = from, writer = writer)

public infix fun <O, CTX, T : Any, P : Any> StructPropertySpec.NonNullable<O, CTX, T, P>.filter(
    predicate: WriterPredicate<O, CTX, P>
): StructPropertySpec.Nullable<O, CTX, T, P> =
    StructPropertySpec.Nullable(name = name, from = from, writer = writer.filter(predicate))
