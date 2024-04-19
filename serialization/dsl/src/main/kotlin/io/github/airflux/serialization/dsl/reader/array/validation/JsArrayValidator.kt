/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.array.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.isValid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsArray

public fun interface JsArrayValidator<EB, O> {
    public fun validate(
        env: JsReaderEnv<EB, O>,
        location: JsLocation,
        source: JsArray
    ): JsValidatorResult

    public fun interface Builder<EB, O> {
        public fun build(): JsArrayValidator<EB, O>
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
public infix fun <EB, O> JsArrayValidator.Builder<EB, O>.or(
    alt: JsArrayValidator.Builder<EB, O>
): JsArrayValidator.Builder<EB, O> {
    val self = this
    return JsArrayValidator.Builder {
        self.build() or alt.build()
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | S      | S      |
 * | S    | F      | F      |
 * | F    | ignore | F      |
 */
public infix fun <EB, O> JsArrayValidator.Builder<EB, O>.and(
    alt: JsArrayValidator.Builder<EB, O>
): JsArrayValidator.Builder<EB, O> {
    val self = this
    return JsArrayValidator.Builder {
        self.build() and alt.build()
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
internal infix fun <EB, O> JsArrayValidator<EB, O>.or(alt: JsArrayValidator<EB, O>): JsArrayValidator<EB, O> {
    val self = this
    return JsArrayValidator { env, location, value ->
        val left = self.validate(env, location, value)
        if (left.isValid()) return@JsArrayValidator valid()

        val right = alt.validate(env, location, value)
        if (right.isValid()) return@JsArrayValidator valid()

        JsValidatorResult.Invalid(left.failure + right.failure)
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | S      | S      |
 * | S    | F      | F      |
 * | F    | ignore | F      |
 */
internal infix fun <EB, O> JsArrayValidator<EB, O>.and(alt: JsArrayValidator<EB, O>): JsArrayValidator<EB, O> {
    val self = this
    return JsArrayValidator { env, location, value ->
        val left = self.validate(env, location, value)
        if (left.isValid())
            alt.validate(env, location, value)
        else
            left
    }
}
