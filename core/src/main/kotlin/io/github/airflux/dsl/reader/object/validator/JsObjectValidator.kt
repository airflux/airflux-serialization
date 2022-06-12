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

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.After
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.Before

@Suppress("unused")
public sealed interface JsObjectValidator {

    public fun interface Before : JsObjectValidator {

        public fun validation(
            context: JsReaderContext,
            location: JsLocation,
            properties: JsObjectProperties,
            input: JsObject
        ): JsResult.Failure?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(other: Before): Before {
            val self = this
            return Before { context, location, properties, input ->
                self.validation(context, location, properties, input)
                    ?.let { error ->
                        other.validation(context, location, properties, input)
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
        public infix fun and(other: Before): Before {
            val self = this
            return Before { context, location, properties, input ->
                val result = self.validation(context, location, properties, input)
                result ?: other.validation(context, location, properties, input)
            }
        }
    }

    public fun interface After : JsObjectValidator {

        public fun validation(
            context: JsReaderContext,
            location: JsLocation,
            properties: JsObjectProperties,
            objectValuesMap: ObjectValuesMap,
            input: JsObject
        ): JsResult.Failure?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(other: After): After {
            val self = this
            return After { context, location, properties, objectValuesMap, input ->
                self.validation(context, location, properties, objectValuesMap, input)
                    ?.let { error ->
                        other.validation(context, location, properties, objectValuesMap, input)
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
        public infix fun and(other: After): After {
            val self = this
            return After { context, location, properties, objectValuesMap, input ->
                val result = self.validation(context, location, properties, objectValuesMap, input)
                result ?: other.validation(context, location, properties, objectValuesMap, input)
            }
        }
    }
}
