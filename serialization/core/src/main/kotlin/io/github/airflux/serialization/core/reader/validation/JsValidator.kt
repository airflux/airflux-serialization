/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv

public fun interface JsValidator<EB, O, CTX, in T> {
    public fun validate(env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation, value: T): ValidationResult
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
public infix fun <EB, O, CTX, T> JsValidator<EB, O, CTX, T>.or(alt: JsValidator<EB, O, CTX, T>): JsValidator<EB, O, CTX, T> {
    val self = this
    return JsValidator { env, context, location, value ->
        when (val left = self.validate(env, context, location, value)) {
            is ValidationResult.Valid -> left
            is ValidationResult.Invalid -> when (val right = alt.validate(env, context, location, value)) {
                is ValidationResult.Valid -> right
                is ValidationResult.Invalid -> ValidationResult.Invalid(left.reason + right.reason)
            }
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
public infix fun <EB, O, CTX, T> JsValidator<EB, O, CTX, T>.and(alt: JsValidator<EB, O, CTX, T>): JsValidator<EB, O, CTX, T> {
    val self = this
    return JsValidator { env, context, location, value ->
        when (val result = self.validate(env, context, location, value)) {
            is ValidationResult.Valid -> alt.validate(env, context, location, value)
            is ValidationResult.Invalid -> result
        }
    }
}
