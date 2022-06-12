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

package io.github.airflux.dsl.reader.configuration

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

public fun objectReaderConfiguration(block: JsObjectReaderConfiguration.Builder.() -> Unit): JsObjectReaderConfiguration =
    JsObjectReaderConfiguration.Builder().apply(block).build()

public class JsObjectReaderConfiguration private constructor(
    public val checkUniquePropertyPath: Boolean,
    public val validation: Validation
) {

    @AirfluxMarker
    public class Builder {
        public var checkUniquePropertyPath: Boolean = false
        private var validation: Validation.Builder = Validation.Builder()

        public fun validation(block: Validation.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): JsObjectReaderConfiguration =
            JsObjectReaderConfiguration(
                checkUniquePropertyPath = checkUniquePropertyPath,
                validation = validation.build()
            )
    }

    public class Validation private constructor(
        public val before: JsObjectValidatorBuilder.Before?,
        public val after: JsObjectValidatorBuilder.After?
    ) {

        @AirfluxMarker
        public class Builder(
            public var before: JsObjectValidatorBuilder.Before? = null,
            public var after: JsObjectValidatorBuilder.After? = null
        ) {
            internal fun build(): Validation = Validation(before, after)
        }
    }

    public companion object {
        public val DEFAULT: JsObjectReaderConfiguration = Builder().build()
    }
}