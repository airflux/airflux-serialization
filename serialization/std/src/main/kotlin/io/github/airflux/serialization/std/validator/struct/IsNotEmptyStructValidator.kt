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

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator

public class IsNotEmptyStructValidator<EB, O> internal constructor() : StructValidator<EB, O>
    where EB : IsNotEmptyStructValidator.ErrorBuilder {

    override fun validate(
        env: JsReaderEnv<EB, O>,
        context: JsContext,
        location: JsLocation,
        properties: StructProperties<EB, O>,
        source: JsStruct
    ): ValidationResult =
        if (source.isEmpty())
            invalid(location = location, error = env.errorBuilders.isNotEmptyStructError())
        else
            valid()

    public fun interface ErrorBuilder {
        public fun isNotEmptyStructError(): ReadingResult.Error
    }
}
