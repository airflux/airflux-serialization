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

package io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification

import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader

public sealed class JsObjectPropertySpec<T> {
    public abstract val path: JsPaths
    public abstract val reader: JsReader<T>

    public class Required<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T>
    ) : JsObjectPropertySpec<T>()

    public class Defaultable<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T>
    ) : JsObjectPropertySpec<T>()

    public class Optional<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T?>
    ) : JsObjectPropertySpec<T?>()

    public class OptionalWithDefault<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T>
    ) : JsObjectPropertySpec<T>()

    public class Nullable<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T?>
    ) : JsObjectPropertySpec<T?>()

    public class NullableWithDefault<T : Any> internal constructor(
        override val path: JsPaths,
        override val reader: JsReader<T?>
    ) : JsObjectPropertySpec<T?>()
}
