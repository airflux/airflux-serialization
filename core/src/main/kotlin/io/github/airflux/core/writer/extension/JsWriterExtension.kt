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

package io.github.airflux.core.writer.extension

import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.JsWriter

fun <T, P : Any> writeAsRequired(receiver: T, getter: (T) -> P, using: JsWriter<P>): JsValue =
    using.write(getter.invoke(receiver))

fun <T, P : Any> writeAsOptional(receiver: T, getter: (T) -> P?, using: JsWriter<P>): JsValue? =
    getter.invoke(receiver)
        ?.let { value -> using.write(value) }

fun <T, P : Any> writeAsNullable(receiver: T, getter: (T) -> P?, using: JsWriter<P>): JsValue {
    val value = getter.invoke(receiver)
    return if (value != null) using.write(value) else JsNull
}

fun <T : Any> arrayWriter(using: JsWriter<T>): JsArrayWriter<Collection<T>> = JsArrayWriter { value ->
    JsArray(items = value.map { item -> using.write(item) })
}
