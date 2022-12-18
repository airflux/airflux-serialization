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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidator
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidatorBuilder

internal class DummyArrayValidatorBuilder<EB, CTX>(result: ReaderResult.Failure?) : ArrayValidatorBuilder<EB, CTX> {

    val validator = Validator<EB, CTX>(result)
    override fun build(): ArrayValidator<EB, CTX> = validator

    internal class Validator<EB, CTX>(val result: ReaderResult.Failure?) : ArrayValidator<EB, CTX> {
        override fun validate(
            env: ReaderEnv<EB, CTX>,
            location: Location,
            source: ArrayNode<*>
        ): ReaderResult.Failure? = result
    }
}
