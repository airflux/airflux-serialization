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

import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.validator.JsObjectValidator

public sealed interface JsObjectValidatorBuilder {

    public fun interface Before : JsObjectValidatorBuilder {
        public fun build(properties: JsObjectProperties): JsObjectValidator.Before
    }

    public fun interface After : JsObjectValidatorBuilder {
        public fun build(properties: JsObjectProperties): JsObjectValidator.After
    }
}

public infix fun JsObjectValidatorBuilder.Before?.or(
    alt: JsObjectValidatorBuilder.Before
): JsObjectValidatorBuilder.Before =
    if (this != null)
        JsObjectValidatorBuilder.Before { properties ->
            this.build(properties).or(alt.build(properties))
        }
    else
        alt

public infix fun JsObjectValidatorBuilder.Before?.and(
    alt: JsObjectValidatorBuilder.Before
): JsObjectValidatorBuilder.Before =
    if (this != null)
        JsObjectValidatorBuilder.Before { properties ->
            this.build(properties).and(alt.build(properties))
        }
    else
        alt

public infix fun JsObjectValidatorBuilder.After?.or(
    alt: JsObjectValidatorBuilder.After
): JsObjectValidatorBuilder.After =
    if (this != null)
        JsObjectValidatorBuilder.After { properties ->
            this.build(properties).or(alt.build(properties))
        }
    else
        alt

public infix fun JsObjectValidatorBuilder.After?.and(
    alt: JsObjectValidatorBuilder.After
): JsObjectValidatorBuilder.After =
    if (this != null)
        JsObjectValidatorBuilder.After { properties ->
            this.build(properties).and(alt.build(properties))
        }
    else
        alt
