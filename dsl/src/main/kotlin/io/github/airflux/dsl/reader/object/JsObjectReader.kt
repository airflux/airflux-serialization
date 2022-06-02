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
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.property.specification.JsObjectPropertySpec
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

@Suppress("unused")
fun interface JsObjectReader<T> : JsReader<T> {

    fun interface TypeBuilder<T> : (JsReaderContext, JsLocation, ObjectValuesMap) -> JsResult<T>

    @AirfluxMarker
    interface Builder<T> {

        var checkUniquePropertyPath: Boolean

        fun validation(block: Validation.Builder.() -> Unit)

        fun <P : Any> property(spec: JsObjectPropertySpec.Required<P>): JsObjectProperty.Required<P>
        fun <P : Any> property(spec: JsObjectPropertySpec.Defaultable<P>): JsObjectProperty.Defaultable<P>
        fun <P : Any> property(spec: JsObjectPropertySpec.Optional<P>): JsObjectProperty.Optional<P>
        fun <P : Any> property(spec: JsObjectPropertySpec.OptionalWithDefault<P>): JsObjectProperty.OptionalWithDefault<P>
        fun <P : Any> property(spec: JsObjectPropertySpec.Nullable<P>): JsObjectProperty.Nullable<P>
        fun <P : Any> property(spec: JsObjectPropertySpec.NullableWithDefault<P>): JsObjectProperty.NullableWithDefault<P>

        fun returns(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): TypeBuilder<T>
    }

    class Validation private constructor(
        val before: JsObjectValidatorBuilder.Before?,
        val after: JsObjectValidatorBuilder.After?
    ) {

        @AirfluxMarker
        class Builder(
            var before: JsObjectValidatorBuilder.Before? = null,
            var after: JsObjectValidatorBuilder.After? = null
        ) {
            internal fun build(): Validation = Validation(before, after)
        }
    }
}
