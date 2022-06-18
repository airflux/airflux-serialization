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

package io.github.airflux.dsl.writer.`object`.builder.property.specification

import io.github.airflux.core.value.JsNull
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.filter
import io.github.airflux.core.writer.predicate.JsPredicate

internal class JsObjectNullablePropertySpec<T : Any, P : Any> private constructor(
    override val name: String,
    override val from: (T) -> P?,
    override val writer: JsWriter<P?>
) : JsObjectPropertySpec.Nullable<T, P> {

    override fun filter(predicate: JsPredicate<P>): JsObjectPropertySpec.Nullable<T, P> =
        JsObjectNullablePropertySpec(name = name, from = from, writer = writer.filter(predicate))

    companion object {

        fun <T : Any, P : Any> of(
            name: String,
            from: (T) -> P?,
            writer: JsWriter<P>
        ): JsObjectPropertySpec.Nullable<T, P> =
            JsObjectNullablePropertySpec(
                name = name,
                from = from,
                writer = { context, location, value ->
                    if (value != null) writer.write(context, location, value) else JsNull
                }
            )
    }
}
