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

import io.github.airflux.serialization.dsl.reader.config.ObjectReaderConfig
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties

public interface ObjectReaderValidatorsBuilder {
    public fun validation(block: ObjectValidatorBuilders.Builder.() -> Unit)
}

internal class ObjectReaderValidatorsBuilderInstance(
    configuration: ObjectReaderConfig
) : ObjectReaderValidatorsBuilder {
    private val builder: ObjectValidatorBuilders.Builder = ObjectValidatorBuilders.Builder(configuration.validation)

    override fun validation(block: ObjectValidatorBuilders.Builder.() -> Unit) {
        builder.block()
    }

    fun build(properties: ObjectProperties): ObjectValidators {
        val builders = builder.build()
        return ObjectValidators(builders.map { builder -> builder.build(properties) })
    }
}