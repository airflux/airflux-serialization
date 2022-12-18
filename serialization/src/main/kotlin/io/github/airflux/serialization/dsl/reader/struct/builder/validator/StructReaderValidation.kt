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

package io.github.airflux.serialization.dsl.reader.struct.builder.validator

import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties

public interface StructReaderValidation<EB, CTX> {

    public fun validation(
        validator: StructValidatorBuilder<EB, CTX>,
        vararg validators: StructValidatorBuilder<EB, CTX>
    ) {
        validation(
            validators = mutableListOf<StructValidatorBuilder<EB, CTX>>().apply {
                add(validator)
                addAll(validators)
            }
        )
    }

    public fun validation(validators: List<StructValidatorBuilder<EB, CTX>>)
}

internal class StructReaderValidationInstance<EB, CTX> : StructReaderValidation<EB, CTX> {
    private val builders = mutableListOf<StructValidatorBuilder<EB, CTX>>()

    override fun validation(validators: List<StructValidatorBuilder<EB, CTX>>) {
        builders.addAll(validators)
    }

    fun build(properties: StructProperties<EB, CTX>): List<StructValidator<EB, CTX>> =
        builders.map { builder -> builder.build(properties) }
            .takeIf { it.isNotEmpty() }
            .orEmpty()
}
