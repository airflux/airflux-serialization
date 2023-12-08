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

package io.github.airflux.serialization.test.dummy

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

public class DummyValidator<EB, O, T>(
    public val result: (JsReaderEnv<EB, O>, JsLocation, T) -> JsValidatorResult
) : JsValidator<EB, O, T> {

    public constructor(result: JsValidatorResult) : this({ _, _, _ -> result })

    override fun validate(
        env: JsReaderEnv<EB, O>,
        location: JsLocation,
        value: T
    ): JsValidatorResult =
        result(env, location, value)

    public companion object {

        @JvmStatic
        public fun <EB, O> isNotEmptyString(error: () -> JsReaderResult.Error): JsValidator<EB, O, String?> =
            DummyValidator { _, location, value ->
                if (value != null) {
                    if (value.isNotEmpty())
                        valid()
                    else
                        invalid(location = location, error = error())
                } else
                    valid()
            }
    }
}
