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

package io.github.airflux.core.reader.validator.std.array

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsValidator

public class IsUniqueArrayValidator<T, K : Any> internal constructor(private val keySelector: (T) -> K) :
    JsValidator<Collection<T>> {

    override fun validation(context: JsReaderContext, location: JsLocation, value: Collection<T>): JsErrors? {
        val failFast = context.failFast
        val errorBuilder = context.getValue(ErrorBuilder)
        val errors = mutableListOf<JsError>()
        val unique = mutableSetOf<K>()
        value.forEachIndexed { index, item ->
            val key = keySelector(item)
            if (!unique.add(key)) errors.add(errorBuilder.build(index, key))
            if (failFast && errors.isNotEmpty()) return JsErrors.of(errors)
        }
        return JsErrors.of(errors)
    }

    public class ErrorBuilder(private val function: (index: Int, value: Any) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(index: Int, value: Any): JsError = function(index, value)

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "IsUniqueArrayValidatorErrorBuilder"
        }
    }
}