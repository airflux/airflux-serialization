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

import io.github.airflux.dsl.reader.validator.JsArrayValidator

public sealed interface JsArrayValidatorBuilder {

    public fun interface Before : JsArrayValidatorBuilder {
        public fun build(): JsArrayValidator.Before
    }

    public fun interface After<T> : JsArrayValidatorBuilder {
        public fun build(): JsArrayValidator.After<T>
    }
}

public infix fun JsArrayValidatorBuilder.Before?.or(
    alt: JsArrayValidatorBuilder.Before
): JsArrayValidatorBuilder.Before =
    if (this != null)
        JsArrayValidatorBuilder.Before { this.build().or(alt.build()) }
    else
        alt

public infix fun JsArrayValidatorBuilder.Before?.and(
    alt: JsArrayValidatorBuilder.Before
): JsArrayValidatorBuilder.Before =
    if (this != null)
        JsArrayValidatorBuilder.Before { this.build().and(alt.build()) }
    else
        alt

public infix fun <T : Any> JsArrayValidatorBuilder.After<T>?.or(
    alt: JsArrayValidatorBuilder.After<T>
): JsArrayValidatorBuilder.After<T> =
    if (this != null)
        JsArrayValidatorBuilder.After { this.build().or(alt.build()) }
    else
        alt

public infix fun <T : Any> JsArrayValidatorBuilder.After<T>?.and(
    alt: JsArrayValidatorBuilder.After<T>
): JsArrayValidatorBuilder.After<T> =
    if (this != null)
        JsArrayValidatorBuilder.After { this.build().and(alt.build()) }
    else
        alt
