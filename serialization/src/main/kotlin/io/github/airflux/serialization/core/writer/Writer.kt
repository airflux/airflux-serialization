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

package io.github.airflux.serialization.core.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.core.writer.predicate.JsPredicate

public fun interface Writer<in T : Any> {

    public fun write(context: WriterContext, location: JsLocation, value: T): ValueNode?
}

internal fun <T : Any> Writer<T>.filter(predicate: JsPredicate<T>): Writer<T> =
    Writer { context, location, value ->
        if (predicate.test(context, location, value))
            this@filter.write(context, location, value)
        else
            null
    }
