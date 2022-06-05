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

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsValue
import io.github.airflux.dsl.reader.array.validator.JsArrayValidator.After
import io.github.airflux.dsl.reader.array.validator.JsArrayValidator.Before

@Suppress("unused")
public sealed interface JsArrayValidator {

    public fun interface Before : JsArrayValidator {

        public fun validation(context: JsReaderContext, input: JsArray<JsValue>): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(alt: Before): Before {
            val self = this
            return Before { context, input ->
                self.validation(context, input)
                    ?.let { error ->
                        alt.validation(context, input)
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
            return Before { context, input ->
                val result = self.validation(context, input)
                result ?: alt.validation(context, input)
            }
        }
    }

    public fun interface After<T> : JsArrayValidator {

        public fun validation(context: JsReaderContext, input: JsArray<JsValue>, items: List<T>): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        public infix fun or(alt: After<T>): After<T> {
            val self = this
            return After { context, input, items ->
                self.validation(context, input, items)
                    ?.let { error ->
                        alt.validation(context, input, items)
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
            return After { context, input, items ->
                val result = self.validation(context, input, items)
                result ?: alt.validation(context, input, items)
            }
        }
    }
}
