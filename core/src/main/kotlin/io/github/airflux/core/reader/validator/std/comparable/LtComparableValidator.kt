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

package io.github.airflux.core.reader.validator.std.comparable

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.contextKeyName
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator

public class LtComparableValidator<T> internal constructor(private val expected: T) : JsValidator<T>
    where T : Number,
          T : Comparable<T> {

    override fun validation(context: JsReaderContext, location: JsLocation, value: T): JsResult.Failure? =
        if (value < expected)
            null
        else {
            val errorBuilder = context.getValue(ErrorBuilder)
            JsResult.Failure(location = location, error = errorBuilder.build(expected, value))
        }

    public class ErrorBuilder(private val function: (expected: Number, actual: Number) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(expected: Number, actual: Number): JsError = function(expected, actual)

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = contextKeyName()
        }
    }
}
