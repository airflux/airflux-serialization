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

package io.github.airflux.common

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsArrayValidator

internal class DummyAfterArrayValidatorBuilder<T>(result: JsResult.Failure?) : JsArrayValidatorBuilder.After<T> {
    val validator = Validator<T>(result)
    override fun build(): JsArrayValidator.After<T> = validator

    internal class Validator<T>(val result: JsResult.Failure?) : JsArrayValidator.After<T> {
        override fun validate(
            context: JsReaderContext,
            location: JsLocation,
            input: JsArray<*>,
            items: List<T>
        ): JsResult.Failure? = result
    }
}
