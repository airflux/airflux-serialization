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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidator
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidatorBuilder

internal class DummyObjectValidatorBuilder(
    override val key: ObjectValidatorBuilder.Key<*>,
    result: ReaderResult.Failure?
) : ObjectValidatorBuilder {

    val validator = Validator(result)
    override fun build(properties: ObjectProperties): ObjectValidator = validator

    internal class Validator(val result: ReaderResult.Failure?) : ObjectValidator {
        override fun validate(
            context: ReaderContext,
            location: Location,
            properties: ObjectProperties,
            input: StructNode
        ): ReaderResult.Failure? = result
    }

    companion object {
        fun <E : ObjectValidatorBuilder> key() = object : ObjectValidatorBuilder.Key<E> {}
    }
}
