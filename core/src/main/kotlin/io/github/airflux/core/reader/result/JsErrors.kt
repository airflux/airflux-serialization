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

package io.github.airflux.core.reader.result

public class JsErrors private constructor(public val items: List<JsError>) {

    public operator fun plus(other: JsErrors): JsErrors = JsErrors(items + other.items)

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsErrors && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    override fun toString(): String = buildString {
        append("JsErrors(items=")
        append(items.toString())
        append(")")
    }

    public companion object {

        public fun of(error: JsError, vararg errors: JsError): JsErrors = if (errors.isEmpty())
            JsErrors(listOf(error))
        else
            JsErrors(listOf(error) + errors.asList())

        public fun of(errors: List<JsError>): JsErrors? = errors.takeIf { it.isNotEmpty() }?.let { JsErrors(it) }
    }
}
