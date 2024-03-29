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
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.NumberFormatErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.toFailure
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.std.reader.env.invalidTypeError

/**
 * Reader for primitive [Long] type.
 */
public fun <EB, O> longReader(): JsReader<EB, O, Long>
    where EB : InvalidTypeErrorBuilder,
          EB : NumberFormatErrorBuilder =
    JsReader { env, location, source ->
        if (source is JsNumeric.Integer)
            source.toLong(env, location)
        else
            env.invalidTypeError(location, expected = JsNumeric.Integer.nameOfType, actual = source.nameOfType)
    }

private fun <EB, O> JsNumeric.Integer.toLong(env: JsReaderEnv<EB, O>, location: JsLocation): JsReaderResult<Long>
    where EB : InvalidTypeErrorBuilder,
          EB : NumberFormatErrorBuilder =
    try {
        get.toLong().toSuccess(location)
    } catch (expected: NumberFormatException) {
        env.errorBuilders.numberFormatError(get, Long::class).toFailure(location = location)
    }
