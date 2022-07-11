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

package io.github.airflux.dsl.reader.validator

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties

public fun interface JsObjectValidator {

    public fun validate(
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
    public infix fun or(alt: JsObjectValidator): JsObjectValidator {
        val self = this
        return JsObjectValidator { context, location, properties, input ->
            self.validate(context, location, properties, input)
                ?.let { error ->
                    alt.validate(context, location, properties, input)
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
    public infix fun and(alt: JsObjectValidator): JsObjectValidator {
        val self = this
        return JsObjectValidator { context, location, properties, input ->
            val result = self.validate(context, location, properties, input)
            result ?: alt.validate(context, location, properties, input)
        }
    }
}
