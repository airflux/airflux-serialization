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

package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.core.reader.JsReader
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths
import io.github.airflux.dsl.reader.`object`.property.specification.JsObjectPropertySpec

public sealed class JsObjectProperty {
    public abstract val path: JsPaths

    public class Required<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.Required<T>) : this(spec.path, spec.reader)
    }

    public class Defaultable<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.Defaultable<T>) : this(spec.path, spec.reader)
    }

    public class Optional<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T?>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.Optional<T>) : this(spec.path, spec.reader)
    }

    public class OptionalWithDefault<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.OptionalWithDefault<T>) : this(spec.path, spec.reader)
    }

    public class Nullable<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T?>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.Nullable<T>) : this(spec.path, spec.reader)
    }

    public class NullableWithDefault<T : Any> private constructor(
        override val path: JsPaths,
        public val reader: JsReader<T?>
    ) : JsObjectProperty() {

        internal constructor(spec: JsObjectPropertySpec.NullableWithDefault<T>) : this(spec.path, spec.reader)
    }
}