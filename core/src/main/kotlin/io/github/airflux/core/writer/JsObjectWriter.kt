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
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.ActionOfWriterIfObjectIsEmpty
import io.github.airflux.core.writer.context.option.actionOfWriterIfObjectIsEmpty

public fun interface JsObjectWriter<in T : Any> : JsWriter<T>

public typealias ActionSelectorIfObjectIsEmpty = (JsWriterContext, JsLocation) -> ActionOfWriterIfObjectIsEmpty.Action

public fun <T : Any> JsObjectWriter<T>.actionIfEmpty(
    selector: ActionSelectorIfObjectIsEmpty = { context, _ -> context.actionOfWriterIfObjectIsEmpty }
): JsObjectWriter<T> = JsObjectWriter { context, location, value ->
    this@actionIfEmpty.actionIfEmpty(context, location, value, selector)
}

private fun <T : Any> JsObjectWriter<T>.actionIfEmpty(
    context: JsWriterContext,
    location: JsLocation,
    value: T,
    selector: ActionSelectorIfObjectIsEmpty
): JsValue? {

    fun JsObject.orElseValue(action: ActionOfWriterIfObjectIsEmpty.Action): JsValue? = when (action) {
        ActionOfWriterIfObjectIsEmpty.Action.NONE -> this
        ActionOfWriterIfObjectIsEmpty.Action.NULL -> JsNull
        ActionOfWriterIfObjectIsEmpty.Action.SKIP -> null
    }

    val result = this.write(context, location, value)
    return if (result != null && result is JsObject && result.isEmpty())
        result.orElseValue(selector(context, location))
    else
        result
}
