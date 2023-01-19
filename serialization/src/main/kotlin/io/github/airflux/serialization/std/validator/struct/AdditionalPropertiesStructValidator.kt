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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator

public class AdditionalPropertiesStructValidator<EB, O, CTX> internal constructor(
    private val names: Set<String>
) : StructValidator<EB, O, CTX>
    where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
          O : FailFastOption {

    override fun validate(
        env: ReaderEnv<EB, O, CTX>,
        location: Location,
        properties: List<StructProperty<EB, O, CTX>>,
        source: StructNode
    ): ReaderResult.Failure? {
        val failFast = env.options.failFast

        val failures = mutableListOf<ReaderResult.Failure>()
        source.forEach { (name, _) ->
            if (name !in names) {
                val failure =
                    ReaderResult.Failure(location.append(name), env.errorBuilders.additionalPropertiesStructError())
                if (failFast) return failure
                failures.add(failure)
            }
        }
        return failures.takeIf { it.isNotEmpty() }?.merge()
    }

    public interface ErrorBuilder {
        public fun additionalPropertiesStructError(): ReaderResult.Error
    }
}
