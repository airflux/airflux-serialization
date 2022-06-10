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

package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.dsl.reader.array.validator.JsArrayValidator
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

public class IsNotEmptyArrayValidator internal constructor() : JsArrayValidator.Before {

    override fun validation(context: JsReaderContext, location: JsLocation, input: JsArray<*>): JsResult.Failure? {
        val errorBuilder = context.getValue(ErrorBuilder)
        return if (input.isEmpty())
            JsResult.Failure(location, errorBuilder.build())
        else
            null
    }

    public class ErrorBuilder(private val function: () -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(): JsError = function()

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "IsNotEmptyArrayValidatorErrorBuilder"
        }
    }
}

internal object IsNotEmptyArrayValidatorBuilder : JsArrayValidatorBuilder.Before {
    private val validator = IsNotEmptyArrayValidator()
    override fun build(): JsArrayValidator.Before = validator
}
