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
import io.github.airflux.dsl.reader.`object`.property.specification.JsReaderPropertySpec

sealed class JsReaderProperty {
    abstract val path: JsPaths

    class Required<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.Required<T>) : this(spec.path, spec.reader)
    }

    class Defaultable<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.Defaultable<T>) : this(spec.path, spec.reader)
    }

    class Optional<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T?>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.Optional<T>) : this(spec.path, spec.reader)
    }

    class OptionalWithDefault<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.OptionalWithDefault<T>) : this(spec.path, spec.reader)
    }

    class Nullable<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T?>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.Nullable<T>) : this(spec.path, spec.reader)
    }

    class NullableWithDefault<T : Any> private constructor(
        override val path: JsPaths,
        val reader: JsReader<T?>
    ) : JsReaderProperty() {

        internal constructor(spec: JsReaderPropertySpec.NullableWithDefault<T>) : this(spec.path, spec.reader)
    }
}
