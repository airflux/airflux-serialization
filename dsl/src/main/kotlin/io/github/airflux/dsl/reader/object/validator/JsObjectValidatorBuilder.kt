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

import io.github.airflux.dsl.reader.`object`.property.JsReaderProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder.After
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder.Before

sealed interface JsObjectValidatorBuilder {

    fun interface Before : JsObjectValidatorBuilder {

        infix fun or(other: Before): Before = Before { properties ->
            this.build(properties).or(other.build(properties))
        }

        infix fun and(other: Before): Before = Before { properties ->
            this.build(properties).and(other.build(properties))
        }

        fun build(properties: JsReaderProperties): JsObjectValidator.Before

        companion object : Before {
            private val empty = JsObjectValidator.Before { _, _, _ -> null }
            override fun build(properties: JsReaderProperties): JsObjectValidator.Before = empty
        }
    }

    fun interface After : JsObjectValidatorBuilder {

        infix fun or(other: After): After = After { properties ->
            this.build(properties).or(other.build(properties))
        }

        infix fun and(other: After): After = After { properties ->
            this.build(properties).and(other.build(properties))
        }

        fun build(properties: JsReaderProperties): JsObjectValidator.After

        companion object : After {
            private val empty = JsObjectValidator.After { _, _, _, _ -> null }
            override fun build(properties: JsReaderProperties): JsObjectValidator.After = empty
        }
    }
}
