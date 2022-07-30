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
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidatorBuilders

public fun arrayReaderConfig(block: ArrayReaderConfig.Builder.() -> Unit): ArrayReaderConfig =
    ArrayReaderConfig.Builder().apply(block).build()

public class ArrayReaderConfig private constructor(public val validation: ArrayValidatorBuilders) {

    @AirfluxMarker
    public class Builder internal constructor() {
        private var validation: ArrayValidatorBuilders.Builder = ArrayValidatorBuilders.Builder()

        public fun validation(block: ArrayValidatorBuilders.Builder.() -> Unit) {
            validation.block()
        }

        internal fun build(): ArrayReaderConfig = ArrayReaderConfig(validation = validation.build())
    }

    public companion object {
        public val DEFAULT: ArrayReaderConfig = Builder().build()
    }
}
