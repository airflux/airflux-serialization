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

package io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification

import io.github.airflux.serialization.core.writer.JsWriter

public sealed interface JsObjectPropertySpec<T : Any, P : Any> {
    public val name: String

    public class NonNullable<T : Any, P : Any> internal constructor(
        override val name: String,
        public val from: (T) -> P,
        public val writer: JsWriter<P>
    ) : JsObjectPropertySpec<T, P>

    public class Optional<T : Any, P : Any> internal constructor(
        override val name: String,
        public val from: (T) -> P?,
        public val writer: JsWriter<P>
    ) : JsObjectPropertySpec<T, P>

    public class Nullable<T : Any, P : Any> internal constructor(
        override val name: String,
        public val from: (T) -> P?,
        public val writer: JsWriter<P>
    ) : JsObjectPropertySpec<T, P>
}
