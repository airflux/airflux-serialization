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

package io.github.airflux.serialization.dsl.reader.array.builder.validator

import io.github.airflux.serialization.dsl.AirfluxMarker

public class ArrayValidatorBuilders private constructor(
    items: List<ArrayValidatorBuilder>
) : List<ArrayValidatorBuilder> by items {

    @AirfluxMarker
    public class Builder internal constructor(items: List<ArrayValidatorBuilder> = emptyList()) {
        private val builders = mutableListOf<ArrayValidatorBuilder>().apply { addAll(items) }

        public operator fun <T : ArrayValidatorBuilder> T.unaryPlus() {
            remove(this)
            builders.add(this)
        }

        public operator fun <T : ArrayValidatorBuilder> T.unaryMinus(): Unit = remove(this)

        internal fun build(): ArrayValidatorBuilders =
            if (builders.isNotEmpty()) ArrayValidatorBuilders(builders) else EMPTY

        private fun <T : ArrayValidatorBuilder> remove(builder: T) {
            val indexBuilder = builders.indexOfFirst { it.key == builder.key }
            if (indexBuilder != -1) builders.removeAt(indexBuilder)
        }

        private companion object {
            private val EMPTY = ArrayValidatorBuilders(emptyList())
        }
    }
}
