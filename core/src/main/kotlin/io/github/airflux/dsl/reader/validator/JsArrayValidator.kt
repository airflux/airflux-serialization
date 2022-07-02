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
import io.github.airflux.core.value.JsArray
import io.github.airflux.dsl.reader.validator.JsArrayValidator.After
import io.github.airflux.dsl.reader.validator.JsArrayValidator.Before

@Suppress("unused")
public sealed interface JsArrayValidator {

    public fun interface Before : JsArrayValidator {

        public fun validation(context: JsReaderContext, location: JsLocation, input: JsArray<*>): JsResult.Failure?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(alt: Before): Before {
            val self = this
            return Before { context, location, input ->
                self.validation(context, location, input)
                    ?.let { error ->
                        alt.validation(context, location, input)
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
        public infix fun and(alt: Before): Before {
            val self = this
            return Before { context, location, input ->
                val result = self.validation(context, location, input)
                result ?: alt.validation(context, location, input)
            }
        }
    }

    public fun interface After<T> : JsArrayValidator {

        public fun validation(
            context: JsReaderContext,
            location: JsLocation,
            input: JsArray<*>,
            items: List<T>
        ): JsResult.Failure?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(alt: After<T>): After<T> {
            val self = this
            return After { context, location, input, items ->
                self.validation(context, location, input, items)
                    ?.let { error ->
                        alt.validation(context, location, input, items)
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
        public infix fun and(alt: After<T>): After<T> {
            val self = this
            return After { context, location, input, items ->
                val result = self.validation(context, location, input, items)
                result ?: alt.validation(context, location, input, items)
            }
        }
    }
}
