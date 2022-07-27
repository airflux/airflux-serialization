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

package io.github.airflux.dsl.reader.array.builder.item.specification

import io.github.airflux.core.reader.JsReader

public sealed class JsArrayItemSpec<out T> {
    public abstract val reader: JsReader<T>

    public class NonNullable<out T> internal constructor(override val reader: JsReader<T>) : JsArrayItemSpec<T>()
    public class Nullable<out T> internal constructor(override val reader: JsReader<T?>) : JsArrayItemSpec<T?>()
}
