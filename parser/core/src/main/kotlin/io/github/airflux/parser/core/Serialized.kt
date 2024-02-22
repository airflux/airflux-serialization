/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.parser.core

@JvmInline
public value class Serialized(private val value: String?) {

    public fun isNone(): Boolean = value == null

    public fun orNull(): String? = value

    public fun orThrow(): String = orThrow { IllegalStateException("No serialized value.") }

    public fun orThrow(builder: () -> Throwable): String = value ?: throw builder()

    public fun orEmptyArray(): String = value ?: "[]"

    public fun orEmptyStruct(): String = value ?: "{}"

    public companion object {
        public val None: Serialized = Serialized(null)
    }
}
