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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.core.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.serialization.core.context.error.JsContextErrorBuilderKey
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.dsl.reader.array.builder.validator.JsArrayValidator

public class MinItemsArrayValidator internal constructor(private val expected: Int) : JsArrayValidator {

    override fun validate(context: JsReaderContext, location: JsLocation, input: ArrayNode<*>): JsResult.Failure? =
        if (input.size < expected) {
            val errorBuilder = context[ErrorBuilder]
            JsResult.Failure(location, errorBuilder.build(expected, input.size))
        } else
            null

    public class ErrorBuilder(private val function: (expected: Int, actual: Int) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(expected: Int, actual: Int): JsError = function(expected, actual)

        public companion object Key : JsContextErrorBuilderKey<ErrorBuilder> {
            override val name: String = errorBuilderName()
        }
    }
}
