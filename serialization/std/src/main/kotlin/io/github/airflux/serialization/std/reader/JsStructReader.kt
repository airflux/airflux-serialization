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

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.std.reader.env.invalidTypeError

/**
 * Reader for the [JsStruct] type.
 */
public object JsStructReader {

    public fun <EB, O> build(): JsReader<EB, O, JsStruct>
        where EB : InvalidTypeErrorBuilder =
        JsReader { env, location, source ->
            if (source is JsStruct)
                success(location, source)
            else
                env.invalidTypeError(location, JsValue.Type.STRUCT, source.type)
        }
}
