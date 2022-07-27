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

package io.github.airflux.serialization.dsl.reader.`object`.builder.validator

import io.github.airflux.serialization.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectProperties

public interface JsObjectReaderValidatorsBuilder {
    public fun validation(block: JsObjectValidatorBuilders.Builder.() -> Unit)
}

internal class JsObjectReaderValidatorsBuilderInstance(
    configuration: JsObjectReaderConfig
) : JsObjectReaderValidatorsBuilder {
    private val builder: JsObjectValidatorBuilders.Builder = JsObjectValidatorBuilders.Builder(configuration.validation)

    override fun validation(block: JsObjectValidatorBuilders.Builder.() -> Unit) {
        builder.block()
    }

    fun build(properties: JsObjectProperties): JsObjectValidators {
        val builders = builder.build()
        return JsObjectValidators(builders.map { builder -> builder.build(properties) })
    }
}
