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

package io.github.airflux.serialization.dsl.reader.config

import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilders

public fun objectReaderConfig(block: JsObjectReaderConfig.Builder.() -> Unit): JsObjectReaderConfig =
    JsObjectReaderConfig.Builder().apply(block).build()

public class JsObjectReaderConfig private constructor(public val validation: JsObjectValidatorBuilders) {

    @AirfluxMarker
    public class Builder internal constructor() {
        private var validation: JsObjectValidatorBuilders.Builder = JsObjectValidatorBuilders.Builder()

        public fun validation(block: JsObjectValidatorBuilders.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsObjectReaderConfig = JsObjectReaderConfig(validation = validation.build())
    }

    public companion object {
        public val DEFAULT: JsObjectReaderConfig = Builder().build()
    }
}
