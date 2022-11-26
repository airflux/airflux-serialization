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

package io.github.airflux.serialization.dsl.reader.struct.builder.validator.std

import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidatorBuilder
import io.github.airflux.serialization.std.validator.struct.AdditionalPropertiesObjectValidator

internal class AdditionalPropertiesObjectValidatorBuilder<EB, CTX> : ObjectValidatorBuilder<EB, CTX>
    where EB : AdditionalPropertiesObjectValidator.ErrorBuilder,
          CTX : FailFastOption {

    override val key: ObjectValidatorBuilder.Key<*> = Key

    override fun build(properties: ObjectProperties<EB, CTX>): ObjectValidator<EB, CTX> {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesObjectValidator(names)
    }

    internal fun ObjectProperties<EB, CTX>.names(): Set<String> {
        fun ObjectProperty<EB, CTX>.names(): List<String> = path.items
            .mapNotNull { path ->
                when (val element = path.elements.first()) {
                    is PropertyPath.Element.Key -> element.get
                    is PropertyPath.Element.Idx -> null
                }
            }

        return flatMap { property -> property.names() }.toSet()
    }

    companion object Key : ObjectValidatorBuilder.Key<AdditionalPropertiesObjectValidatorBuilder<*, *>>
}
