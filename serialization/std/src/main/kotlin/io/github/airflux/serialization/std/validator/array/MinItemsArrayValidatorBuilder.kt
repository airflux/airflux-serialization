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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.dsl.reader.array.validation.ArrayValidator

internal class MinItemsArrayValidatorBuilder<EB, O>(private val value: Int) : ArrayValidator.Builder<EB, O>
    where EB : MinItemsArrayValidator.ErrorBuilder {

    override fun build(): ArrayValidator<EB, O> = MinItemsArrayValidator(value)
}
