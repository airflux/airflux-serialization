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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.dsl.reader.array.validation.ArrayValidator

internal class DummyArrayValidatorBuilder<EB, O, CTX>(result: ValidationResult) : ArrayValidator.Builder<EB, O, CTX> {

    private val validator = Validator<EB, O, CTX>(result)
    override fun build(): ArrayValidator<EB, O, CTX> = validator

    internal class Validator<EB, O, CTX>(val result: ValidationResult) : ArrayValidator<EB, O, CTX> {
        override fun validate(
            env: JsReaderEnv<EB, O>,
            context: CTX,
            location: JsLocation,
            source: JsArray
        ): ValidationResult = result
    }

    companion object {

        @JvmStatic
        internal fun <EB, O, CTX> minItems(
            expected: Int,
            error: (expected: Int, actual: Int) -> ReadingResult.Error
        ): ArrayValidator.Builder<EB, O, CTX> =
            ArrayValidator.Builder {
                ArrayValidator { _, _, location, source ->
                    if (source.size < expected)
                        return@ArrayValidator invalid(location = location, error = error(expected, source.size))
                    else
                        valid()
                }
            }
    }
}
