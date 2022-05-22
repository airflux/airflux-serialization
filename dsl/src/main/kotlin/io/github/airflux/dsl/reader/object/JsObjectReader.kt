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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidators

@Suppress("unused")
fun interface JsObjectReader<T> : JsReader<T> {

    fun interface TypeBuilder<T> : (JsReaderContext, ObjectValuesMap) -> JsResult<T>

    @AirfluxMarker
    interface Builder<T> {

        fun validation(init: JsObjectValidators.Builder.() -> Unit)

        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Required<P>): JsReaderProperty.Required<P>
        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Defaultable<P>): JsReaderProperty.Defaultable<P>
        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Optional<P>): JsReaderProperty.Optional<P>
        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.OptionalWithDefault<P>): JsReaderProperty.OptionalWithDefault<P>
        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Nullable<P>): JsReaderProperty.Nullable<P>
        fun <P : Any> property(builder: JsReaderPropertySpecBuilder.NullableWithDefault<P>): JsReaderProperty.NullableWithDefault<P>

        fun build(builder: ObjectValuesMap.() -> JsResult<T>): TypeBuilder<T>
    }
}
