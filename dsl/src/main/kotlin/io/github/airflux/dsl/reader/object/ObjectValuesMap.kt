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

import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperty

@Suppress("TooManyFunctions")
sealed interface ObjectValuesMap {

    val isEmpty: Boolean
    val isNotEmpty: Boolean
    val size: Int

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.Required<T>): T
    operator fun <T : Any> JsObjectReaderProperty.Required<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.Defaultable<T>): T
    operator fun <T : Any> JsObjectReaderProperty.Defaultable<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.Optional<T>): T?
    operator fun <T : Any> JsObjectReaderProperty.Optional<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.OptionalWithDefault<T>): T
    operator fun <T : Any> JsObjectReaderProperty.OptionalWithDefault<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.Nullable<T>): T?
    operator fun <T : Any> JsObjectReaderProperty.Nullable<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(property: JsObjectReaderProperty.NullableWithDefault<T>): T?
    operator fun <T : Any> JsObjectReaderProperty.NullableWithDefault<T>.unaryPlus(): T? = get(this)
}
