/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.After
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.Before
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.value.JsObject

@Suppress("unused")
sealed interface JsObjectValidator {

    fun interface Before : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            context: JsReaderContext,
            properties: List<JsReaderProperty>,
            input: JsObject
        ): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        infix fun or(other: Before): Before {
            val self = this
            return Before { configuration, context, properties, input ->
                self.validation(configuration, context, properties, input)
                    ?.let { error ->
                        other.validation(configuration, context, properties, input)
                            ?.let { error + it }
                    }
            }
        }

        /*
         * | This | Other  | Result |
         * |------|--------|--------|
         * | S    | S      | S      |
         * | S    | F      | F      |
         * | F    | ignore | F      |
         */
        infix fun and(other: Before): Before {
            val self = this
            return Before { configuration, context, properties, input ->
                when (val result = self.validation(configuration, context, properties, input)) {
                    null -> other.validation(configuration, context, properties, input)
                    else -> result
                }
            }
        }
    }

    fun interface After : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            context: JsReaderContext,
            properties: List<JsReaderProperty>,
            objectValuesMap: ObjectValuesMap,
            input: JsObject
        ): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        infix fun or(other: After): After {
            val self = this
            return After { configuration, context, properties, objectValuesMap, input ->
                self.validation(configuration, context, properties, objectValuesMap, input)
                    ?.let { error ->
                        other.validation(configuration, context, properties, objectValuesMap, input)
                            ?.let { error + it }
                    }
            }
        }

        /*
         * | This | Other  | Result |
         * |------|--------|--------|
         * | S    | S      | S      |
         * | S    | F      | F      |
         * | F    | ignore | F      |
         */
        infix fun and(other: After): After {
            val self = this
            return After { configuration, context, properties, objectValuesMap, input ->
                when (val result = self.validation(configuration, context, properties, objectValuesMap, input)) {
                    null -> other.validation(configuration, context, properties, objectValuesMap, input)
                    else -> result
                }
            }
        }
    }
}
