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

package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder.After
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder.Before

sealed interface JsObjectValidatorBuilder {

    fun interface Before : JsObjectValidatorBuilder {
        fun build(properties: JsObjectReaderProperties): JsObjectValidator.Before
    }

    fun interface After : JsObjectValidatorBuilder {
        fun build(properties: JsObjectReaderProperties): JsObjectValidator.After
    }
}

infix fun Before?.or(alt: Before): Before =
    if (this == null)
        alt
    else
        Before { properties ->
            this.build(properties).or(alt.build(properties))
        }

infix fun Before?.and(alt: Before): Before =
    if (this == null)
        alt
    else
        Before { properties ->
            this.build(properties).and(alt.build(properties))
        }

infix fun After?.or(alt: After): After =
    if (this == null)
        alt
    else
        After { properties ->
            this.build(properties).or(alt.build(properties))
        }

infix fun After?.and(alt: After): After =
    if (this == null)
        alt
    else
        After { properties ->
            this.build(properties).and(alt.build(properties))
        }
