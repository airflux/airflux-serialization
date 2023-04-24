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

package io.github.airflux.serialization.dsl.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validation.Validated
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidatorBuilder

internal class DummyStructValidatorBuilder<EB, O, CTX>(result: Validated) : StructValidatorBuilder<EB, O, CTX> {

    private val validator = Validator<EB, O, CTX>(result)

    override fun build(properties: StructProperties<EB, O, CTX>): StructValidator<EB, O, CTX> = validator

    internal class Validator<EB, O, CTX>(val result: Validated) : StructValidator<EB, O, CTX> {
        override fun validate(
            env: ReaderEnv<EB, O>,
            context: CTX,
            location: Location,
            properties: StructProperties<EB, O, CTX>,
            source: StructNode
        ): Validated = result
    }

    companion object {
        internal fun <EB, O, CTX> additionalProperties(
            nameProperties: Set<String>,
            error: ReaderResult.Error
        ): StructValidatorBuilder<EB, O, CTX> =
            object : StructValidatorBuilder<EB, O, CTX> {
                override fun build(properties: StructProperties<EB, O, CTX>): StructValidator<EB, O, CTX> =
                    StructValidator { _, _, location, _, node ->
                        node.forEach { (name, _) ->
                            if (name !in nameProperties)
                                return@StructValidator invalid(location = location.append(name), error = error)
                        }
                        valid()
                    }
            }
    }
}
