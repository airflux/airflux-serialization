/*
 * Copyright 2021-2023 Maxim Sambulat.
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
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.predicate.WriterPredicate

public fun interface Writer<O, CTX, in T> {
    public fun write(env: WriterEnv<O>, context: CTX, location: JsLocation, source: T): JsValue?
}

public fun <O, CTX, T, R> Writer<O, CTX, T>.contramap(transform: (R) -> T): Writer<O, CTX, R> =
    Writer { env, context, location, source ->
        this@contramap.write(env, context, location, transform(source))
    }

public fun <O, CTX, T : Any> Writer<O, CTX, T>.nullable(): Writer<O, CTX, T?> =
    Writer { env, context, location, source ->
        if (source != null) this@nullable.write(env, context, location, source) else JsNull
    }

public fun <O, CTX, T : Any> Writer<O, CTX, T>.optional(): Writer<O, CTX, T?> =
    Writer { env, context, location, source ->
        if (source != null) this@optional.write(env, context, location, source) else null
    }

public fun <O, CTX, T> Writer<O, CTX, T>.filter(predicate: WriterPredicate<O, CTX, T & Any>): Writer<O, CTX, T?> =
    Writer { env, context, location, source ->
        if (source != null && predicate.test(env, context, location, source))
            this@filter.write(env, context, location, source)
        else
            null
    }
