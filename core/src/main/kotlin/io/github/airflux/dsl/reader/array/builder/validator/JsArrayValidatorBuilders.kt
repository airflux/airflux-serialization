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

package io.github.airflux.dsl.reader.array.builder.validator

import io.github.airflux.dsl.AirfluxMarker

public class JsArrayValidatorBuilders private constructor(
    items: List<JsArrayValidatorBuilder>
) : List<JsArrayValidatorBuilder> by items {

    @AirfluxMarker
    public class Builder internal constructor(items: List<JsArrayValidatorBuilder> = emptyList()) {
        private val builders = mutableListOf<JsArrayValidatorBuilder>().apply { addAll(items) }

        public operator fun <T : JsArrayValidatorBuilder> T.unaryPlus() {
            remove(this)
            builders.add(this)
        }

        public operator fun <T : JsArrayValidatorBuilder> T.unaryMinus(): Unit = remove(this)

        internal fun build(): JsArrayValidatorBuilders =
            if (builders.isNotEmpty()) JsArrayValidatorBuilders(builders) else EMPTY

        private fun <T : JsArrayValidatorBuilder> remove(builder: T) {
            val indexBuilder = builders.indexOfFirst { it.key == builder.key }
            if (indexBuilder != -1) builders.removeAt(indexBuilder)
        }

        private companion object {
            private val EMPTY = JsArrayValidatorBuilders(emptyList())
        }
    }
}
