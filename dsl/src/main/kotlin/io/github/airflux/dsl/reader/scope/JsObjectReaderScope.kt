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
import io.github.airflux.dsl.reader.`object`.JsObjectValidation

fun objectReaderScope(block: JsObjectReaderScope.Builder.() -> Unit): JsObjectReaderScope =
    JsObjectReaderScope.Builder().apply(block).build()

class JsObjectReaderScope private constructor(
    val validation: JsObjectValidation
) {

    @AirfluxMarker
    class Builder {
        private var validation: JsObjectValidation.Builder = JsObjectValidation.Builder()

        fun validation(block: JsObjectValidation.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsObjectReaderScope = JsObjectReaderScope(validation.build())
    }
}
