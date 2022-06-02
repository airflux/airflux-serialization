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

package io.github.airflux.dsl.reader.scope

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

fun objectReaderConfiguration(block: JsObjectReaderConfiguration.Builder.() -> Unit): JsObjectReaderConfiguration =
    JsObjectReaderConfiguration.Builder().apply(block).build()

class JsObjectReaderConfiguration private constructor(
    val checkUniquePropertyPath: Boolean,
    val validation: Validation
) {

    @AirfluxMarker
    class Builder {
        var checkUniquePropertyPath: Boolean = false
        private var validation: Validation.Builder = Validation.Builder()

        fun validation(block: Validation.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsObjectReaderConfiguration =
            JsObjectReaderConfiguration(
                checkUniquePropertyPath = checkUniquePropertyPath,
                validation = validation.build()
            )
    }

    class Validation private constructor(
        val before: JsObjectValidatorBuilder.Before?,
        val after: JsObjectValidatorBuilder.After?
    ) {

        @AirfluxMarker
        class Builder(
            var before: JsObjectValidatorBuilder.Before? = null,
            var after: JsObjectValidatorBuilder.After? = null
        ) {
            internal fun build(): Validation = Validation(before, after)
        }
    }

    companion object {
        val DEFAULT = Builder().build()
    }
}
