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

package io.github.airflux.dsl.reader.array.validator

import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder.After
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder.Before

sealed interface JsArrayValidatorBuilder {

    fun interface Before : JsArrayValidatorBuilder {
        fun build(): JsArrayValidator.Before
    }

    fun interface After<T> : JsArrayValidatorBuilder {
        fun build(): JsArrayValidator.After<T>
    }
}

infix fun Before?.or(alt: Before): Before =
    if (this != null)
        Before { this.build().or(alt.build()) }
    else
        alt

infix fun Before?.and(alt: Before): Before =
    if (this != null)
        Before { this.build().and(alt.build()) }
    else
        alt

infix fun <T : Any> After<T>?.or(alt: After<T>): After<T> =
    if (this != null)
        After { this.build().or(alt.build()) }
    else
        alt

infix fun <T : Any> After<T>?.and(alt: After<T>): After<T> =
    if (this != null)
        After { this.build().and(alt.build()) }
    else
        alt
