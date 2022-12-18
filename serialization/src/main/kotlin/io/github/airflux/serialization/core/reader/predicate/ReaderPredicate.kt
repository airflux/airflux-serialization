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

package io.github.airflux.serialization.core.reader.predicate

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv

public fun interface ReaderPredicate<EB, CTX, T> {
    public fun test(env: ReaderEnv<EB, CTX>, location: Location, value: T): Boolean
}

public infix fun <EB, CTX, T> ReaderPredicate<EB, CTX, T>.or(
    other: ReaderPredicate<EB, CTX, T>
): ReaderPredicate<EB, CTX, T> {
    val self = this
    return ReaderPredicate { env, location, value ->
        val result = self.test(env, location, value)
        if (result) true else other.test(env, location, value)
    }
}

public infix fun <EB, CTX, T> ReaderPredicate<EB, CTX, T>.and(
    other: ReaderPredicate<EB, CTX, T>
): ReaderPredicate<EB, CTX, T> {
    val self = this
    return ReaderPredicate { env, location, value ->
        val result = self.test(env, location, value)
        if (result) other.test(env, location, value) else false
    }
}
