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

package io.github.airflux.serialization.dsl.reader.struct.builder.property

import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.StructPropertySpec

public sealed class StructProperty<out EB, out CTX> {
    public abstract val path: PropertyPaths

    public class Required<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.Required<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class RequiredIf<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T?>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.RequiredIf<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class Defaultable<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.Defaultable<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class Optional<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T?>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.Optional<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class OptionalWithDefault<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.OptionalWithDefault<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class Nullable<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T?>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.Nullable<EB, CTX, T>) : this(spec.path, spec.reader)
    }

    public class NullableWithDefault<EB, CTX, T : Any> private constructor(
        override val path: PropertyPaths,
        public val reader: Reader<EB, CTX, T?>
    ) : StructProperty<EB, CTX>() {

        internal constructor(spec: StructPropertySpec.NullableWithDefault<EB, CTX, T>) : this(spec.path, spec.reader)
    }
}
