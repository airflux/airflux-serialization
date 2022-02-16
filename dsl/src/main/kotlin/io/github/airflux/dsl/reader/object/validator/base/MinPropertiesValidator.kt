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

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.JsReaderBuilder
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator

@Suppress("unused")
class MinPropertiesValidator private constructor(private val value: Int, private val errorBuilder: ErrorBuilder) :
    JsObjectValidator.After {

    override fun validation(
        options: JsReaderBuilder.Options,
        context: JsReaderContext,
        properties: List<JsReaderProperty>,
        objectValuesMap: ObjectValuesMap,
        input: JsObject
    ): JsErrors? =
        if (objectValuesMap.size < value)
            JsErrors.of(errorBuilder.build(expected = value, actual = objectValuesMap.size))
        else
            null

    class Builder(private val errorBuilder: ErrorBuilder) {

        operator fun invoke(min: Int): JsObjectValidator.After = MinPropertiesValidator(min, errorBuilder)
    }

    fun interface ErrorBuilder {
        fun build(expected: Int, actual: Int): JsError
    }
}
