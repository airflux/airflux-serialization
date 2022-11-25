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

@Suppress("TooManyFunctions")
public sealed interface PropertyValues<EB, CTX> {

    public val isEmpty: Boolean
    public val isNotEmpty: Boolean
    public val size: Int

    public infix operator fun <T : Any> get(property: ObjectProperty.Required<EB, CTX, T>): T
    public operator fun <T : Any> ObjectProperty.Required<EB, CTX, T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Defaultable<EB, CTX, T>): T
    public operator fun <T : Any> ObjectProperty.Defaultable<EB, CTX, T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Optional<EB, CTX, T>): T?
    public operator fun <T : Any> ObjectProperty.Optional<EB, CTX, T>.unaryPlus(): T? = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.OptionalWithDefault<EB, CTX, T>): T
    public operator fun <T : Any> ObjectProperty.OptionalWithDefault<EB, CTX, T>.unaryPlus(): T = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.Nullable<EB, CTX, T>): T?
    public operator fun <T : Any> ObjectProperty.Nullable<EB, CTX, T>.unaryPlus(): T? = get(this)

    public infix operator fun <T : Any> get(property: ObjectProperty.NullableWithDefault<EB, CTX, T>): T?
    public operator fun <T : Any> ObjectProperty.NullableWithDefault<EB, CTX, T>.unaryPlus(): T? = get(this)
}
