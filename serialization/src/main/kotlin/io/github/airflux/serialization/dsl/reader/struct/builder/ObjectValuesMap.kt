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

package io.github.airflux.serialization.dsl.reader.struct.builder

import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty

@Suppress("TooManyFunctions")
public sealed interface ObjectValuesMap {

    public val isEmpty: Boolean
    public val isNotEmpty: Boolean
    public val size: Int

    public infix operator fun <T : Any> get(property: ObjectProperty.Required<T>): T
    public operator fun <T : Any> ObjectProperty.Required<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Defaultable<T>): T
    public operator fun <T : Any> ObjectProperty.Defaultable<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Optional<T>): T?
    public operator fun <T : Any> ObjectProperty.Optional<T>.unaryPlus(): T? = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.OptionalWithDefault<T>): T
    public operator fun <T : Any> ObjectProperty.OptionalWithDefault<T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Nullable<T>): T?
    public operator fun <T : Any> ObjectProperty.Nullable<T>.unaryPlus(): T? = get(this)

    public
    infix operator fun <T : Any> get(property: ObjectProperty.NullableWithDefault<T>): T?
    public operator fun <T : Any> ObjectProperty.NullableWithDefault<T>.unaryPlus(): T? = get(this)
}
