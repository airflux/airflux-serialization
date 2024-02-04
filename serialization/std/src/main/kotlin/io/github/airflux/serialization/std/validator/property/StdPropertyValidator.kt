/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.std.validator.property

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.validation.JsValidator

public object StdPropertyValidator {

    /**
     * Validation of a value if the predicate returns the true value,
     * if a value is present (not equal to null), then an error, otherwise a success.
     */
    @JvmStatic
    public fun <EB, O, T> forbidden(
        predicate: (env: JsReaderEnv<EB, O>, location: JsLocation) -> Boolean
    ): JsValidator<EB, O, T>
        where EB : ForbiddenPropertyValidator.ErrorBuilder = ForbiddenPropertyValidator(predicate)

    /**
     * Validation of a value if the predicate returns the true value,
     * if a value is missing (equal to null), then an error, otherwise a success.
     */
    @JvmStatic
    public fun <EB, O, T> mandatory(
        predicate: (env: JsReaderEnv<EB, O>, location: JsLocation) -> Boolean
    ): JsValidator<EB, O, T>
        where EB : MandatoryPropertyValidator.ErrorBuilder = MandatoryPropertyValidator(predicate)
}
