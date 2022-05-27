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

package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.core.reader.context.JsReaderAbstractContextElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.ErrorBuilder
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

@Suppress("unused")
object IsNotEmpty : JsObjectValidatorBuilder.After {

    private val validator = JsObjectValidator.After { context, _, values, _ ->
        val errorBuilder = context.getValue(Error)
        if (values.isEmpty) JsErrors.of(errorBuilder.build()) else null
    }

    override fun build(properties: List<JsReaderProperty>): JsObjectValidator.After = validator

    class Error(private val function: () -> JsError) :
        JsReaderAbstractContextElement<Error>(key = Error),
        ErrorBuilder {

        fun build(): JsError = function()

        companion object Key : JsReaderContext.Key<Error> {
            override val name: String = "IsNotEmptyErrorBuilder"
        }
    }
}
