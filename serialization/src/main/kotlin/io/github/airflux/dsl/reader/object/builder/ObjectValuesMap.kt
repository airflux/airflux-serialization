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

package io.github.airflux.dsl.reader.`object`.builder

import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperty

@Suppress("TooManyFunctions")
public sealed interface ObjectValuesMap {

    public val isEmpty: Boolean
    public val isNotEmpty: Boolean
    public val size: Int

    public infix operator fun <T : Any> get(property: JsObjectProperty.Required<T>): T
    public operator fun <T : Any> JsObjectProperty.Required<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: JsObjectProperty.Defaultable<T>): T
    public operator fun <T : Any> JsObjectProperty.Defaultable<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: JsObjectProperty.Optional<T>): T?
    public operator fun <T : Any> JsObjectProperty.Optional<T>.unaryPlus(): T? = get(this)

    public infix operator fun <T : Any> get(property: JsObjectProperty.OptionalWithDefault<T>): T
    public operator fun <T : Any> JsObjectProperty.OptionalWithDefault<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: JsObjectProperty.Nullable<T>): T?
    public operator fun <T : Any> JsObjectProperty.Nullable<T>.unaryPlus(): T? = get(this)

    public
    infix operator fun <T : Any> get(property: JsObjectProperty.NullableWithDefault<T>): T?
    public operator fun <T : Any> JsObjectProperty.NullableWithDefault<T>.unaryPlus(): T? = get(this)
}
