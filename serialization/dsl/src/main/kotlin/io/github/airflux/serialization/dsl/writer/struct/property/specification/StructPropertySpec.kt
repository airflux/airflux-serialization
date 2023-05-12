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

package io.github.airflux.serialization.dsl.writer.struct.property.specification

import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.filter
import io.github.airflux.serialization.core.writer.predicate.JsPredicate

public fun <O, CTX, T : Any, P : Any> nonNullable(
    name: String,
    from: T.() -> P,
    writer: JsWriter<O, CTX, P>
): StructPropertySpec<O, CTX, T, P> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithoutContext(from), writer = writer)

public fun <O, CTX, T : Any, P : Any> nonNullable(
    name: String,
    from: T.(CTX) -> P,
    writer: JsWriter<O, CTX, P>
): StructPropertySpec<O, CTX, T, P> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor.WithContext(from), writer = writer)

public fun <O, CTX, T : Any, P : Any> nullable(
    name: String,
    from: T.() -> P?,
    writer: JsWriter<O, CTX, P?>
): StructPropertySpec<O, CTX, T, P?> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor(from), writer = writer)

public fun <O, CTX, T : Any, P : Any> nullable(
    name: String,
    from: T.(CTX) -> P?,
    writer: JsWriter<O, CTX, P?>
): StructPropertySpec<O, CTX, T, P?> =
    StructPropertySpec(name = name, from = StructPropertySpec.Extractor(from), writer = writer)

public class StructPropertySpec<O, CTX, T, P> internal constructor(
    public val name: String,
    public val from: Extractor<CTX, T, P>,
    public val writer: JsWriter<O, CTX, P>
) {

    public sealed class Extractor<CTX, T, P> {

        internal class WithoutContext<CTX, T, P>(val extractor: T.() -> P) : Extractor<CTX, T, P>()
        internal class WithContext<CTX, T, P>(val extractor: T.(CTX) -> P) : Extractor<CTX, T, P>()

        public companion object {

            @JvmStatic
            public operator fun <CTX, T, P> invoke(extractor: T.() -> P): Extractor<CTX, T, P> =
                WithoutContext(extractor)

            @JvmStatic
            public operator fun <CTX, T, P> invoke(extractor: T.(CTX) -> P): Extractor<CTX, T, P> =
                WithContext(extractor)
        }
    }
}

public fun <O, CTX, T, P> StructPropertySpec<O, CTX, T, P>.filter(
    predicate: JsPredicate<O, CTX, P & Any>
): StructPropertySpec<O, CTX, T, P> =
    StructPropertySpec(name = this.name, from = this.from, writer = writer.filter(predicate))
