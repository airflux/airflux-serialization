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

package io.github.airflux.core.reader.validator.std.string

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsValidator

public class IsAStringValidator internal constructor(private val predicate: (String) -> Boolean) : JsValidator<String> {

    override fun validation(context: JsReaderContext, location: JsLocation, value: String): JsErrors? =
        if (!predicate(value)) {
            val errorBuilder = context.getValue(ErrorBuilder)
            JsErrors.of(errorBuilder.build(value))
        } else
            null

    public class ErrorBuilder(private val function: (value: String) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(value: String): JsError = function(value)

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "IsAStringValidatorErrorBuilder"
        }
    }
}