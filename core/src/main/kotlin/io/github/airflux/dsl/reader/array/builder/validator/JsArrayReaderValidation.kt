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

package io.github.airflux.dsl.reader.array.builder.validator

import io.github.airflux.dsl.reader.config.JsArrayReaderConfig

public interface JsArrayReaderValidation {
    public fun validation(block: JsArrayValidation.Builder.() -> Unit)
}

internal class JsArrayReaderValidationInstance(configuration: JsArrayReaderConfig) : JsArrayReaderValidation {
    private val builder: JsArrayValidation.Builder = JsArrayValidation.Builder(configuration.validation)

    override fun validation(block: JsArrayValidation.Builder.() -> Unit) {
        builder.block()
    }

    fun buildValidators(): JsArrayValidators {
        val validation = builder.build()
        return JsArrayValidators(validation.map { builder -> builder.build() })
    }
}
