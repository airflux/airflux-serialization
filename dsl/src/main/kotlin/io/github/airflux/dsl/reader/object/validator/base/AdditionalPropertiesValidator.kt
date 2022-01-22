/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator

@Suppress("unused")
class AdditionalPropertiesValidator private constructor(
    private val names: Set<String>,
    private val errorBuilder: ErrorBuilder
) : JsObjectValidator.Before {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        context: JsReaderContext,
        properties: List<JsReaderProperty>,
        input: JsObject
    ): JsErrors? {
        val unknownProperties = mutableListOf<String>()
        input.forEach { (name, _) ->
            if (name !in names) {
                unknownProperties.add(name)
                if (configuration.failFast) return@forEach
            }
        }
        return unknownProperties.takeIf { it.isNotEmpty() }
            ?.let { JsErrors.of(errorBuilder.build(it)) }
    }

    class Builder(private val errorBuilder: ErrorBuilder) {

        operator fun invoke(properties: List<JsReaderProperty>): JsObjectValidator.Before =
            AdditionalPropertiesValidator(properties.names(), errorBuilder)

        private fun List<JsReaderProperty>.names(): Set<String> =
            mapNotNull { property -> property.name() }
                .toSet()

        private fun JsReaderProperty.name(): String? = path.firstOrNull()
            ?.let {
                when (it) {
                    is PathElement.Key -> it.get
                    is PathElement.Idx -> null
                }
            }
    }

    fun interface ErrorBuilder {
        fun build(properties: List<String>): JsError
    }
}
