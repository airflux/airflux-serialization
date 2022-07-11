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

package io.github.airflux.std.validator.`object`

import io.github.airflux.core.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.context.error.JsContextErrorBuilderKey
import io.github.airflux.core.context.error.errorBuilderName
import io.github.airflux.core.context.error.get
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.validator.JsObjectValidator

public class IsNotEmptyObjectValidator internal constructor() : JsObjectValidator {

    override fun validate(
        context: JsReaderContext,
        location: JsLocation,
        properties: JsObjectProperties,
        input: JsObject
    ): JsResult.Failure? =
        if (input.isEmpty()) {
            val errorBuilder = context[ErrorBuilder]
            JsResult.Failure(location, errorBuilder.build())
        } else
            null

    public class ErrorBuilder(private val function: () -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(): JsError = function()

        public companion object Key : JsContextErrorBuilderKey<ErrorBuilder> {
            override val name: String = errorBuilderName()
        }
    }
}
