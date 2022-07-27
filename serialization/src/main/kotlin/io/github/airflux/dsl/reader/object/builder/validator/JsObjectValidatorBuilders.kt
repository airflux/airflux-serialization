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

package io.github.airflux.dsl.reader.`object`.builder.validator

import io.github.airflux.dsl.AirfluxMarker

public class JsObjectValidatorBuilders private constructor(
    items: List<JsObjectValidatorBuilder>
) : List<JsObjectValidatorBuilder> by items {

    @AirfluxMarker
    public class Builder internal constructor(items: List<JsObjectValidatorBuilder> = emptyList()) {
        private val builders = mutableListOf<JsObjectValidatorBuilder>().apply { addAll(items) }

        public operator fun <T : JsObjectValidatorBuilder> T.unaryPlus() {
            remove(this)
            builders.add(this)
        }

        public operator fun <T : JsObjectValidatorBuilder> T.unaryMinus(): Unit = remove(this)

        internal fun build(): JsObjectValidatorBuilders =
            if (builders.isNotEmpty()) JsObjectValidatorBuilders(builders) else EMPTY

        private fun <T : JsObjectValidatorBuilder> remove(builder: T) {
            val indexBuilder = builders.indexOfFirst { it.key == builder.key }
            if (indexBuilder != -1) builders.removeAt(indexBuilder)
        }

        private companion object {
            private val EMPTY = JsObjectValidatorBuilders(emptyList())
        }
    }
}
