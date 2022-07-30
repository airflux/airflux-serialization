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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.core.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.serialization.core.context.error.ContextErrorBuilderKey
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.validator.Validator

public class IsNotBlankStringValidator internal constructor() : Validator<String> {

    override fun validate(context: ReaderContext, location: Location, value: String): JsResult.Failure? =
        if (value.isNotBlank())
            null
        else {
            val errorBuilder = context[ErrorBuilder]
            JsResult.Failure(location = location, error = errorBuilder.build())
        }

    public class ErrorBuilder(private val function: () -> JsResult.Error) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(): JsResult.Error = function()

        public companion object Key : ContextErrorBuilderKey<ErrorBuilder> {
            override val name: String = errorBuilderName()
        }
    }
}
