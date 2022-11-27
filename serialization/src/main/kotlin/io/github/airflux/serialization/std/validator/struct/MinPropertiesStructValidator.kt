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
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructValidator

public class MinPropertiesStructValidator<EB, CTX> internal constructor(private val value: Int) :
    StructValidator<EB, CTX>
    where EB : MinPropertiesStructValidator.ErrorBuilder {

    override fun validate(
        env: ReaderEnv<EB, CTX>,
        location: Location,
        properties: StructProperties<EB, CTX>,
        source: StructNode
    ): ReaderResult.Failure? =
        if (source.count < value)
            ReaderResult.Failure(location, env.errorBuilders.minPropertiesStructError(value, source.count))
        else
            null

    public interface ErrorBuilder {
        public fun minPropertiesStructError(expected: Int, actual: Int): ReaderResult.Error
    }
}
