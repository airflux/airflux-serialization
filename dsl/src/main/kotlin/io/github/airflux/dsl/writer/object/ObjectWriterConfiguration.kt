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

package io.github.airflux.dsl.writer.`object`

import io.github.airflux.dsl.AirfluxMarker

class ObjectWriterConfiguration private constructor(
    val skipPropertyIfArrayIsEmpty: Boolean,
    val skipPropertyIfObjectIsEmpty: Boolean,
    val writeNullIfArrayIsEmpty: Boolean,
    val writeNullIfObjectIsEmpty: Boolean
) {

    @AirfluxMarker
    class Builder internal constructor(base: ObjectWriterConfiguration) {

        var skipPropertyIfArrayIsEmpty = base.skipPropertyIfArrayIsEmpty
        var skipPropertyIfObjectIsEmpty = base.skipPropertyIfObjectIsEmpty
        var writeNullIfArrayIsEmpty = base.writeNullIfArrayIsEmpty
        var writeNullIfObjectIsEmpty = base.writeNullIfObjectIsEmpty

        internal fun build(): ObjectWriterConfiguration = ObjectWriterConfiguration(
            skipPropertyIfArrayIsEmpty = skipPropertyIfArrayIsEmpty,
            skipPropertyIfObjectIsEmpty = skipPropertyIfObjectIsEmpty,
            writeNullIfArrayIsEmpty = writeNullIfArrayIsEmpty,
            writeNullIfObjectIsEmpty = writeNullIfObjectIsEmpty
        )
    }

    companion object {
        val Default = ObjectWriterConfiguration(
            skipPropertyIfArrayIsEmpty = false,
            skipPropertyIfObjectIsEmpty = false,
            writeNullIfArrayIsEmpty = false,
            writeNullIfObjectIsEmpty = false
        )

        fun build(block: Builder.() -> Unit): ObjectWriterConfiguration = Builder(Default).apply(block).build()
    }
}
