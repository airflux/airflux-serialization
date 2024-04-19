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

package io.github.airflux.serialization.dsl.reader.struct.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.isValid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties

public fun interface JsStructValidator<EB, O> {

    public fun validate(
        env: JsReaderEnv<EB, O>,
        location: JsLocation,
        properties: StructProperties<EB, O>,
        source: JsStruct
    ): JsValidatorResult

    public fun interface Builder<EB, O> {
        public fun build(properties: StructProperties<EB, O>): JsStructValidator<EB, O>
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
public infix fun <EB, O> JsStructValidator.Builder<EB, O>.or(
    alt: JsStructValidator.Builder<EB, O>
): JsStructValidator.Builder<EB, O> {
    val self = this
    return JsStructValidator.Builder { properties ->
        self.build(properties) or alt.build(properties)
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | S      | S      |
 * | S    | F      | F      |
 * | F    | ignore | F      |
 */
public infix fun <EB, O> JsStructValidator.Builder<EB, O>.and(
    alt: JsStructValidator.Builder<EB, O>
): JsStructValidator.Builder<EB, O> {
    val self = this
    return JsStructValidator.Builder { properties ->
        self.build(properties) and alt.build(properties)
    }
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
internal infix fun <EB, O> JsStructValidator<EB, O>.or(alt: JsStructValidator<EB, O>): JsStructValidator<EB, O> {
    val self = this
    return JsStructValidator { env, location, properties, value ->
        val left = self.validate(env, location, properties, value)
        if (left.isValid()) return@JsStructValidator valid()

        val right = alt.validate(env, location, properties, value)
        if (right.isValid()) return@JsStructValidator valid()

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
internal infix fun <EB, O> JsStructValidator<EB, O>.and(alt: JsStructValidator<EB, O>): JsStructValidator<EB, O> {
    val self = this
    return JsStructValidator { env, location, properties, value ->
        val left = self.validate(env, location, properties, value)
        if (left.isValid())
            alt.validate(env, location, properties, value)
        else
            left
    }
}
