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

package io.github.airflux.serialization.dsl.reader.`object`.builder.validator.std

import io.github.airflux.serialization.core.path.PropertyPathElement
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidator
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidatorBuilder
import io.github.airflux.serialization.std.validator.`object`.AdditionalPropertiesObjectValidator

internal class AdditionalPropertiesObjectValidatorBuilder : ObjectValidatorBuilder {

    override val key: ObjectValidatorBuilder.Key<*> = Key

    override fun build(properties: ObjectProperties): ObjectValidator {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesObjectValidator(names)
    }

    internal fun ObjectProperties.names(): Set<String> {
        fun ObjectProperty.names(): List<String> = path.items
            .mapNotNull { path ->
                when (val element = path.elements.first()) {
                    is PropertyPathElement.Key -> element.get
                    is PropertyPathElement.Idx -> null
                }
            }

        return flatMap { property -> property.names() }.toSet()
    }

    companion object Key : ObjectValidatorBuilder.Key<AdditionalPropertiesObjectValidatorBuilder>
}
