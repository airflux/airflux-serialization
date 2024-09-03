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

package io.github.airflux.serialization.dsl.writer.struct.property.specification

import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.filter
import io.github.airflux.serialization.core.writer.predicate.JsPredicate

public fun <O, T : Any, P : Any> nonNullable(
    name: String,
    from: T.() -> P,
    writer: JsWriter<O, P>
): StructPropertySpec<O, T, P> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithoutEnv(from), writer = writer)

public fun <O, T : Any, P : Any> nonNullable(
    name: String,
    from: T.(JsWriterEnv<O>) -> P,
    writer: JsWriter<O, P>
): StructPropertySpec<O, T, P> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithEnv(from), writer = writer)

public fun <O, T : Any, P : Any> nullable(
    name: String,
    from: T.() -> P?,
    writer: JsWriter<O, P?>
): StructPropertySpec<O, T, P?> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithoutEnv(from), writer = writer)

public fun <O, T : Any, P : Any> nullable(
    name: String,
    from: T.(JsWriterEnv<O>) -> P?,
    writer: JsWriter<O, P?>
): StructPropertySpec<O, T, P?> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithEnv(from), writer = writer)

public class StructPropertySpec<O, T, P> internal constructor(
    public val name: String,
    public val from: Extractor<O, T, P>,
    public val writer: JsWriter<O, P>
) {

    public sealed class Extractor<O, T, P> {

        internal class WithoutEnv<O, T, P>(val extractor: T.() -> P) : Extractor<O, T, P>()
        internal class WithEnv<O, T, P>(val extractor: T.(JsWriterEnv<O>) -> P) : Extractor<O, T, P>()
    }
}

public fun <O, T, P> StructPropertySpec<O, T, P>.filter(
    predicate: JsPredicate<O, P & Any>
): StructPropertySpec<O, T, P> =
    StructPropertySpec(name = this.name, from = this.from, writer = writer.filter(predicate))
