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

package io.github.airflux.dsl.reader.`object`.builder.validator

import io.github.airflux.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties

public interface JsObjectReaderValidation {
    public fun validation(block: JsObjectValidators.Builder.() -> Unit)
}

internal class JsObjectReaderValidationBuilder(configuration: JsObjectReaderConfig) : JsObjectReaderValidation {
    private val validation: JsObjectValidators.Builder = configuration.validation
        .let { JsObjectValidators.Builder(before = it.before, after = it.after) }

    override fun validation(block: JsObjectValidators.Builder.() -> Unit) {
        validation.block()
    }

    fun build(properties: JsObjectProperties): JsObjectValidators = validation.build(properties)
}
