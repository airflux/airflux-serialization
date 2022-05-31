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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

@Suppress("TooManyFunctions")
sealed interface ObjectValuesMap {

    val isEmpty: Boolean
    val isNotEmpty: Boolean
    val size: Int

    infix operator fun <T : Any> get(property: JsReaderProperty.Required<T>): T
    operator fun <T : Any> JsReaderProperty.Required<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsReaderProperty.Defaultable<T>): T
    operator fun <T : Any> JsReaderProperty.Defaultable<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsReaderProperty.Optional<T>): T?
    operator fun <T : Any> JsReaderProperty.Optional<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(property: JsReaderProperty.OptionalWithDefault<T>): T
    operator fun <T : Any> JsReaderProperty.OptionalWithDefault<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsReaderProperty.Nullable<T>): T?
    operator fun <T : Any> JsReaderProperty.Nullable<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(property: JsReaderProperty.NullableWithDefault<T>): T?
    operator fun <T : Any> JsReaderProperty.NullableWithDefault<T>.unaryPlus(): T? = get(this)
}
