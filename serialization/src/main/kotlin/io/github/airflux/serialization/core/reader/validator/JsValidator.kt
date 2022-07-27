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

package io.github.airflux.serialization.core.reader.validator

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult

@Suppress("unused")
public fun interface JsValidator<in T> {

    public fun validate(context: JsReaderContext, location: JsLocation, value: T): JsResult.Failure?
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
public infix fun <T> JsValidator<T>.or(other: JsValidator<T>): JsValidator<T> {
    val self = this
    return JsValidator { context, location, value ->
        self.validate(context, location, value)
            ?.let { error ->
                other.validate(context, location, value)
                    ?.let { error + it }
            }
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | S      | S      |
 * | S    | F      | F      |
 * | F    | ignore | F      |
 */
public infix fun <T> JsValidator<T>.and(other: JsValidator<T>): JsValidator<T> {
    val self = this
    return JsValidator { context, location, value ->
        val result = self.validate(context, location, value)
        result ?: other.validate(context, location, value)
    }
}
