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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.serialization.core.context.error.ContextErrorBuilderKey
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator

public class MaxPropertiesObjectValidator internal constructor(private val value: Int) : ObjectValidator {

    override fun validate(
        context: ReaderContext,
        location: Location,
        properties: ObjectProperties,
        source: ObjectNode
    ): ReaderResult.Failure? =
        if (source.count > value) {
            val errorBuilder = context[ErrorBuilder]
            ReaderResult.Failure(location, errorBuilder.build(value, source.count))
        } else
            null

    public class ErrorBuilder(private val function: (expected: Int, actual: Int) -> ReaderResult.Error) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(expected: Int, actual: Int): ReaderResult.Error = function(expected, actual)

        public companion object Key : ContextErrorBuilderKey<ErrorBuilder> {
            override val name: String = errorBuilderName()
        }
    }
}
