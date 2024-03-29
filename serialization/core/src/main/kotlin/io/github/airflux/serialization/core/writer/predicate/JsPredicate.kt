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

package io.github.airflux.serialization.core.writer.predicate

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.writer.env.JsWriterEnv

public fun interface JsPredicate<O, T> {
    public fun test(env: JsWriterEnv<O>, location: JsLocation, value: T): Boolean
}

public infix fun <O, T> JsPredicate<O, T>.or(
    alt: JsPredicate<O, T>
): JsPredicate<O, T> {
    val self = this
    return JsPredicate { env, location, value ->
        self.test(env, location, value) || alt.test(env, location, value)
    }
}

public infix fun <O, T> JsPredicate<O, T>.and(
    alt: JsPredicate<O, T>
): JsPredicate<O, T> {
    val self = this
    return JsPredicate { env, location, value ->
        self.test(env, location, value) && alt.test(env, location, value)
    }
}
