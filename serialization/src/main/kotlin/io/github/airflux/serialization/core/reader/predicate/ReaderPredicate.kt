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
import io.github.airflux.serialization.core.reader.context.ReaderContext

@Suppress("unused")
public fun interface ReaderPredicate<T> {

    public fun test(context: ReaderContext, location: Location, value: T): Boolean

    public infix fun or(other: ReaderPredicate<T>): ReaderPredicate<T> {
        val self = this
        return ReaderPredicate { context, location, value ->
            val result = self.test(context, location, value)
            if (result) result else other.test(context, location, value)
        }
    }

    public infix fun and(other: ReaderPredicate<T>): ReaderPredicate<T> {
        val self = this
        return ReaderPredicate { context, location, value ->
            val result = self.test(context, location, value)
            if (result) other.test(context, location, value) else result
        }
    }
}
