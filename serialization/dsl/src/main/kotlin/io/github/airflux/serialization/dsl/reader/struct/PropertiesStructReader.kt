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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties

internal class PropertiesStructReader<EB, O, T>(
    override val properties: StructProperties<EB, O>,
    private val typeBuilder: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
) : AbstractStructReader<EB, O, T>()
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> {
        val failFast = env.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        val values = PropertyValuesInstance<EB, O>()
            .apply {
                properties.forEach { property ->
                    property.read(env, location, source)
                        .fold(
                            onFailure = { failure ->
                                if (failFast) return failure
                                failureAccumulator += failure
                            },
                            onSuccess = { success ->
                                this[property] = success.value
                            }
                        )
                }
            }

        return failureAccumulator ?: typeBuilder(values, env, location)
    }
}
