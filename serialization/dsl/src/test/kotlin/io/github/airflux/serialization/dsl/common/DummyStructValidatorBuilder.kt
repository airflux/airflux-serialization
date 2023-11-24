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

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator

internal class DummyStructValidatorBuilder<EB, O>(result: ValidationResult) : StructValidator.Builder<EB, O> {

    private val validator = Validator<EB, O>(result)

    override fun build(properties: StructProperties<EB, O>): StructValidator<EB, O> = validator

    internal class Validator<EB, O>(val result: ValidationResult) : StructValidator<EB, O> {
        override fun validate(
            env: JsReaderEnv<EB, O>,
            context: JsContext,
            location: JsLocation,
            properties: StructProperties<EB, O>,
            source: JsStruct
        ): ValidationResult = result
    }

    companion object {

        @JvmStatic
        internal fun <EB, O> additionalProperties(
            nameProperties: Set<String>,
            error: JsReaderResult.Error
        ): StructValidator.Builder<EB, O> =
            StructValidator.Builder {
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
