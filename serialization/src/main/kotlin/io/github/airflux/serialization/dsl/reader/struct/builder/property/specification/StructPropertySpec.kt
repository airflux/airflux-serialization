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

package io.github.airflux.serialization.dsl.reader.struct.builder.property.specification

import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader

public sealed class StructPropertySpec<EB, CTX, T> {
    public abstract val path: PropertyPaths
    public abstract val reader: Reader<EB, CTX, T>

    public class Required<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T>
    ) : StructPropertySpec<EB, CTX, T>()

    public class RequiredIf<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T?>
    ) : StructPropertySpec<EB, CTX, T?>()

    public class Defaultable<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T>
    ) : StructPropertySpec<EB, CTX, T>()

    public class Optional<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T?>
    ) : StructPropertySpec<EB, CTX, T?>()

    public class OptionalWithDefault<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T>
    ) : StructPropertySpec<EB, CTX, T>()
}
