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

package io.github.airflux.core.reader.validator.extension

import io.github.airflux.core.common.identity
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.reader.validator.JsValidator

infix fun <T> JsReader<T>.validation(validator: JsValidator<T>): JsReader<T> =
    JsReader { context, location, input ->
        this@validation.read(context, location, input)
            .validation(context, validator)
    }

fun <T> JsResult<T>.validation(validator: JsValidator<T>): JsResult<T> =
    validation(context = JsReaderContext(), validator = validator)

fun <T> JsResult<T>.validation(context: JsReaderContext, validator: JsValidator<T>): JsResult<T> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { result ->
            val errors = validator.validation(context, result.location, result.value)
            if (errors != null) JsResult.Failure(location = result.location, errors = errors) else result
        }
    )
