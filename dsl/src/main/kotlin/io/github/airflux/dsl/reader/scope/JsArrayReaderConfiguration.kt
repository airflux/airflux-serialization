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
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

@Suppress("unused")
fun arrayReaderConfiguration(block: JsArrayReaderConfiguration.Builder.() -> Unit): JsArrayReaderConfiguration =
    JsArrayReaderConfiguration.Builder().apply(block).build()

class JsArrayReaderConfiguration private constructor(
    val validation: Validation
) {

    @AirfluxMarker
    class Builder {
        private var validation: Validation.Builder = Validation.Builder()

        fun validation(block: Validation.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsArrayReaderConfiguration =
            JsArrayReaderConfiguration(
                validation = validation.build()
            )
    }

    class Validation private constructor(
        val before: JsArrayValidatorBuilder.Before?
    ) {

        @AirfluxMarker
        class Builder(
            var before: JsArrayValidatorBuilder.Before? = null
        ) {
            internal fun build(): Validation = Validation(before)
        }
    }

    companion object {
        val DEFAULT: JsArrayReaderConfiguration = Builder().build()
    }
}
