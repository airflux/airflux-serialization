/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidatorBuilder

internal class AdditionalPropertiesStructValidatorBuilder<EB, O, CTX> : StructValidatorBuilder<EB, O, CTX>
    where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
          O : FailFastOption {

    override fun build(properties: StructProperties<EB, O, CTX>): StructValidator<EB, O, CTX> {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesStructValidator(names)
    }

    private fun StructProperties<EB, O, CTX>.names(): Set<String> {
        fun StructProperty<EB, O, CTX, *>.names(): List<String> = paths.items
            .filter { path -> path.head is PropertyPath.Element.Key }
            .map { path -> (path.head as PropertyPath.Element.Key).get }

        return flatMap { property -> property.names() }.toSet()
    }
}
