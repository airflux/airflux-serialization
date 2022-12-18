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

package io.github.airflux.serialization.dsl.reader.array.builder.validator

public interface ArrayReaderValidation<EB, CTX> {

    public fun validation(
        validator: ArrayValidatorBuilder<EB, CTX>,
        vararg validators: ArrayValidatorBuilder<EB, CTX>
    ) {
        validation(
            validators = mutableListOf<ArrayValidatorBuilder<EB, CTX>>().apply {
                add(validator)
                addAll(validators)
            }
        )
    }

    public fun validation(validators: List<ArrayValidatorBuilder<EB, CTX>>)
}

internal class ArrayReaderValidationInstance<EB, CTX> : ArrayReaderValidation<EB, CTX> {

    private val builders = mutableListOf<ArrayValidatorBuilder<EB, CTX>>()

    override fun validation(validators: List<ArrayValidatorBuilder<EB, CTX>>) {
        builders.addAll(validators)
    }

    fun build(): List<ArrayValidator<EB, CTX>> =
        builders.map { builder -> builder.build() }
            .takeIf { it.isNotEmpty() }
            .orEmpty()
}
