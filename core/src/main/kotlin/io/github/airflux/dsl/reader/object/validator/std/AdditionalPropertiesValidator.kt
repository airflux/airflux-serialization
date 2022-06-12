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

package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

public class AdditionalPropertiesValidator internal constructor(
    private val names: Set<String>
) : JsObjectValidator.Before {

    override fun validation(
        context: JsReaderContext,
        location: JsLocation,
        properties: JsObjectProperties,
        input: JsObject
    ): JsResult.Failure? {
        val failFast = context.failFast
        val errorBuilder = context.getValue(ErrorBuilder)

        val failures = mutableListOf<JsResult.Failure>()
        input.forEach { (name, _) ->
            if (name !in names) {
                val failure = JsResult.Failure(location.append(name), errorBuilder.build())
                if (failFast) return failure
                failures.add(failure)
            }
        }
        return failures.takeIf { it.isNotEmpty() }?.merge()
    }

    public class ErrorBuilder(private val function: () -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(): JsError = function()

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "AdditionalPropertiesValidatorErrorBuilder"
        }
    }
}

internal object AdditionalPropertiesValidatorBuilder : JsObjectValidatorBuilder.Before {

    override fun build(properties: JsObjectProperties): JsObjectValidator.Before {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesValidator(names)
    }

    internal fun JsObjectProperties.names(): Set<String> {
        fun JsObjectProperty.names(): List<String> = path.items
            .mapNotNull { path ->
                when (val element = path.elements.first()) {
                    is PathElement.Key -> element.get
                    is PathElement.Idx -> null
                }
            }

        return flatMap { property -> property.names() }.toSet()
    }
}