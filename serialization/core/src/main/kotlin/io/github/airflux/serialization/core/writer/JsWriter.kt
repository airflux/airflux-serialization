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

package io.github.airflux.serialization.core.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.predicate.JsPredicate

public fun interface JsWriter<O, in T> {
    public fun write(env: JsWriterEnv<O>, location: JsLocation, source: T): JsValue?
}

public fun <O, T, R> JsWriter<O, T>.contramap(transform: (R) -> T): JsWriter<O, R> =
    JsWriter { env, location, source ->
        this@contramap.write(env, location, transform(source))
    }

public fun <O, T : Any> JsWriter<O, T>.nullable(): JsWriter<O, T?> =
    JsWriter { env, location, source ->
        if (source != null) this@nullable.write(env, location, source) else JsNull
    }

public fun <O, T : Any> JsWriter<O, T>.optional(): JsWriter<O, T?> =
    JsWriter { env, location, source ->
        if (source != null) this@optional.write(env, location, source) else null
    }

public fun <O, T> JsWriter<O, T>.filter(predicate: JsPredicate<O, T & Any>): JsWriter<O, T?> =
    JsWriter { env, location, source ->
        if (source != null && predicate.test(env, location, source))
            this@filter.write(env, location, source)
        else
            null
    }
