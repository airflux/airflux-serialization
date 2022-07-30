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

import io.github.airflux.serialization.dsl.reader.config.ArrayReaderConfig

public interface ArrayReaderValidatorsBuilder {
    public fun validation(block: JsArrayValidatorBuilders.Builder.() -> Unit)
}

internal class ArrayReaderValidatorsBuilderInstance(
    configuration: ArrayReaderConfig
) : ArrayReaderValidatorsBuilder {
    private val builder: JsArrayValidatorBuilders.Builder = JsArrayValidatorBuilders.Builder(configuration.validation)

    override fun validation(block: JsArrayValidatorBuilders.Builder.() -> Unit) {
        builder.block()
    }

    fun build(): JsArrayValidators {
        val builders = builder.build()
        return JsArrayValidators(builders.map { builder -> builder.build() })
    }
}
