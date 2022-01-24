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

package io.github.airflux.core.reader

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsValue

@Suppress("unused")
fun interface JsReader<T> {

    /**
     * Convert the [JsValue] into a T
     */
    fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T>

    /**
     * Create a new [JsReader] which maps the value produced by this [JsReader].
     *
     * @param[R] The type of the value produced by the new [JsReader].
     * @param transform the function applied on the result of the current instance,
     * if successful
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun <R> map(transform: (T) -> R): JsReader<R> =
        JsReader { context, location, input -> read(context, location, input).map(transform) }

    /**
     * Creates a new [JsReader], based on this one, which first executes this
     * [JsReader] logic then, if this [JsReader] resulted in a [JsError], runs
     * the other [JsReader] on the [JsValue].
     *
     * @param other the [JsReader] to run if this one gets a [JsError]
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun or(other: JsReader<T>): JsReader<T> = JsReader { context, location, input ->
        read(context, location, input)
            .recovery { failure ->
                other.read(context, location, input)
                    .recovery { alternative -> failure + alternative }
            }
    }
}
