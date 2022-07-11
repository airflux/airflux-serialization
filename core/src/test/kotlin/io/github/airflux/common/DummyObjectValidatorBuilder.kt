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

package io.github.airflux.common

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsObjectValidator

internal class DummyObjectValidatorBuilder(
    override val key: JsObjectValidatorBuilder.Key<*>,
    result: JsResult.Failure?
) : JsObjectValidatorBuilder {

    val validator = Validator(result)
    override fun build(properties: JsObjectProperties): JsObjectValidator = validator

    internal class Validator(val result: JsResult.Failure?) : JsObjectValidator {
        override fun validate(
            context: JsReaderContext,
            location: JsLocation,
            properties: JsObjectProperties,
            input: JsObject
        ): JsResult.Failure? = result
    }

    companion object {
        fun <E : JsObjectValidatorBuilder> key() = object : JsObjectValidatorBuilder.Key<E> {}
    }
}
