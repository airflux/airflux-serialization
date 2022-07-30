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

package io.github.airflux.serialization.dsl.writer.config

import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionBuilderIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty

public fun arrayWriterConfig(block: ArrayWriterConfig.Builder.() -> Unit): ArrayWriterConfig =
    ArrayWriterConfig.Builder().apply(block).build()

public class ArrayWriterConfig private constructor(
    public val options: Options
) {

    @AirfluxMarker
    public class Builder internal constructor() {
        public var actionIfEmpty: WriterActionBuilderIfResultIsEmpty = returnEmptyValue

        internal fun build(): ArrayWriterConfig = ArrayWriterConfig(
            options = Options(actionIfEmpty = actionIfEmpty)
        )

        internal companion object {
            private val returnEmptyValue: WriterActionBuilderIfResultIsEmpty =
                { _, _ -> WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE }
        }
    }

    public class Options internal constructor(
        public val actionIfEmpty: WriterActionBuilderIfResultIsEmpty
    )

    public companion object {
        public val DEFAULT: ArrayWriterConfig = Builder().build()
    }
}
