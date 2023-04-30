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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.ReadingResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.toInvalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator

public class AdditionalPropertiesStructValidator<EB, O, CTX> internal constructor(
    private val names: Set<String>
) : StructValidator<EB, O, CTX>
    where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
          O : FailFastOption {

    override fun validate(
        env: ReaderEnv<EB, O>,
        context: CTX,
        location: JsLocation,
        properties: StructProperties<EB, O, CTX>,
        source: StructNode
    ): ValidationResult {
        val failFast = env.options.failFast

        val failures = mutableListOf<ReadingResult.Failure>()
        source.forEach { (name, _) ->
            if (name !in names) {
                val failure =
                    ReadingResult.Failure(location.append(name), env.errorBuilders.additionalPropertiesStructError())
                if (failFast) return failure.toInvalid() else failures.add(failure)
            }
        }

        return if (failures.isEmpty()) valid() else failures.merge().toInvalid()
    }

    public interface ErrorBuilder {
        public fun additionalPropertiesStructError(): ReadingResult.Error
    }
}
