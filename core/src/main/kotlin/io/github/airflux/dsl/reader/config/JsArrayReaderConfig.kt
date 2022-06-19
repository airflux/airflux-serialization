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

package io.github.airflux.dsl.reader.config

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder

@Suppress("unused")
public fun arrayReaderConfig(block: JsArrayReaderConfig.Builder.() -> Unit): JsArrayReaderConfig =
    JsArrayReaderConfig.Builder().apply(block).build()

public class JsArrayReaderConfig private constructor(public val validation: Validation) {

    @AirfluxMarker
    public class Builder internal constructor() {
        private var validation: Validation.Builder = Validation.Builder()

        public fun validation(block: Validation.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsArrayReaderConfig =
            JsArrayReaderConfig(
                validation = validation.build()
            )
    }

    public class Validation private constructor(
        public val before: JsArrayValidatorBuilder.Before?
    ) {

        @AirfluxMarker
        public class Builder internal constructor(
            public var before: JsArrayValidatorBuilder.Before? = null
        ) {
            internal fun build(): Validation = Validation(before)
        }
    }

    public companion object {
        public val DEFAULT: JsArrayReaderConfig = Builder().build()
    }
}
