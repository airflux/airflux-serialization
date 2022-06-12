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

package io.github.airflux.core.reader.validator.std.`object`

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.contextKeyName
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsObjectValidator
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties

public class MaxPropertiesObjectValidator internal constructor(private val value: Int) : JsObjectValidator.After {

    override fun validation(
        context: JsReaderContext,
        location: JsLocation,
        properties: JsObjectProperties,
        objectValuesMap: ObjectValuesMap,
        input: JsObject
    ): JsResult.Failure? {
        val errorBuilder = context.getValue(ErrorBuilder)
        return if (objectValuesMap.size > value)
            JsResult.Failure(location, errorBuilder.build(value, objectValuesMap.size))
        else
            null
    }

    public class ErrorBuilder(private val function: (expected: Int, actual: Int) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(expected: Int, actual: Int): JsError = function(expected, actual)

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = contextKeyName()
        }
    }
}
