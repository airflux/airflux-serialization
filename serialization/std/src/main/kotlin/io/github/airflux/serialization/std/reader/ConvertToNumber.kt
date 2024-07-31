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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.NumberFormatErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.std.reader.env.invalidTypeError

internal inline fun <EB, O, reified T> JsValue.tryConvertToNumber(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    converter: String.() -> T
): JsReaderResult<T>
    where EB : InvalidTypeErrorBuilder,
          EB : NumberFormatErrorBuilder =
    if (this is JsNumber)
        this.tryConvertToNumber(env, location, converter)
    else
        env.invalidTypeError(location, expected = JsValue.Type.NUMBER, actual = this.type)

internal inline fun <EB, O, reified T> JsNumber.tryConvertToNumber(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    converter: String.() -> T
): JsReaderResult<T>
    where EB : InvalidTypeErrorBuilder,
          EB : NumberFormatErrorBuilder =
    try {
        success(location = location, value = converter(this.get))
    } catch (expected: NumberFormatException) {
        failure(location = location, error = env.errorBuilders.numberFormatError(get, T::class))
    }
