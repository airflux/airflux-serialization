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

package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseStringValidators {

    fun minLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length < expected) JsErrors.of(error(expected, value.length)) else null
        }

    fun maxLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length > expected) JsErrors.of(error(expected, value.length)) else null
        }

    fun isNotEmpty(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isEmpty()) JsErrors.of(error()) else null
        }

    fun isNotBlank(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isBlank()) JsErrors.of(error()) else null
        }

    fun pattern(pattern: Regex, error: (value: String, pattern: Regex) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (pattern.matches(value)) null else JsErrors.of(error(value, pattern))
        }

    fun isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (predicate(value)) null else JsErrors.of(error(value))
        }
}
