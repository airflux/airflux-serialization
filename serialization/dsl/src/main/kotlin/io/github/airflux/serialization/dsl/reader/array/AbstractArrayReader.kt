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

package io.github.airflux.serialization.dsl.reader.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsValue

internal abstract class AbstractArrayReader<EB, O, T> : JsArrayReader<EB, O, T>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    final override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue): JsReaderResult<List<T>> =
        if (source is JsArray)
            read(env, location, source)
        else
            failure(env, location, source)

    abstract fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsArray): JsReaderResult<List<T>>

    private fun failure(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue) =
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(expected = JsValue.Type.ARRAY, actual = source.type)
        )
}
