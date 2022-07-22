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

package io.github.airflux.core.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.ActionOfWriterIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.actionOfWriterIfArrayIsEmpty

public fun interface JsArrayWriter<in T> : JsWriter<List<T>> {
    override fun write(context: JsWriterContext, location: JsLocation, value: List<T>): JsValue?
}

public typealias ActionSelectorIfArrayIsEmpty = (JsWriterContext, JsLocation) -> ActionOfWriterIfArrayIsEmpty.Action

public fun <T> JsArrayWriter<T>.actionIfEmpty(
    selector: ActionSelectorIfArrayIsEmpty = { context, _ -> context.actionOfWriterIfArrayIsEmpty }
): JsArrayWriter<T> = JsArrayWriter { context, location, value ->
    this@actionIfEmpty.actionIfEmpty(context, location, value, selector)
}

private fun <T> JsArrayWriter<T>.actionIfEmpty(
    context: JsWriterContext,
    location: JsLocation,
    value: List<T>,
    selector: ActionSelectorIfArrayIsEmpty
): JsValue? {

    fun JsArray<*>.orElseValue(action: ActionOfWriterIfArrayIsEmpty.Action): JsValue? = when (action) {
        ActionOfWriterIfArrayIsEmpty.Action.NONE -> this
        ActionOfWriterIfArrayIsEmpty.Action.NULL -> JsNull
        ActionOfWriterIfArrayIsEmpty.Action.SKIP -> null
    }

    val result = this.write(context, location, value)
    return if (result != null && result is JsArray<*> && result.isEmpty())
        result.orElseValue(selector(context, location))
    else
        result
}
