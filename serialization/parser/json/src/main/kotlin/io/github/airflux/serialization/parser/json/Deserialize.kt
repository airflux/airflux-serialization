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

package io.github.airflux.serialization.parser.json

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.parser.json.lexer.JsInputString
import io.github.airflux.serialization.parser.json.lexer.JsSourceInput

public fun <EB, O, T> String.deserialize(env: JsReaderEnv<EB, O>, reader: JsReader<EB, O, T>): JsReaderResult<T>
    where EB : ParsingErrorBuilder,
          O : PropertyNamePoolOption =
    JsInputString(this).deserialize(env, reader)

public fun <EB, O, T> JsSourceInput.deserialize(env: JsReaderEnv<EB, O>, reader: JsReader<EB, O, T>): JsReaderResult<T>
    where EB : ParsingErrorBuilder,
          O : PropertyNamePoolOption {
    val pool =
        if (env.options.usePropertyNamePool) PropertyNamePool() else null
    val parser = JsParser(this, pool)
    return when (val result = parser.parse()) {
        is JsParserResult.Success -> {
            val value = result.value
            if (value != null)
                reader.read(env, JsLocation.Root, value)
            else
                JsReaderResult.Failure(location = JsLocation.Root, error = env.errorBuilders.contextMissingError())
        }

        is JsParserResult.Failure -> {
            val error = result.error
            JsReaderResult.Failure(
                location = JsLocation.Root,
                error = env.errorBuilders.parsingError(
                    description = error.description,
                    position = error.position,
                    line = error.line,
                    column = error.column
                )
            )
        }
    }
}
