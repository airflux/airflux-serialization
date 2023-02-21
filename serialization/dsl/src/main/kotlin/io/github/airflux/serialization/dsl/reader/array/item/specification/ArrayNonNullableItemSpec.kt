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

package io.github.airflux.serialization.dsl.reader.array.item.specification

import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <EB, O, CTX, T : Any> nonNullable(reader: Reader<EB, O, CTX, T>): ArrayItemSpec.NonNullable<EB, O, CTX, T> =
    ArrayItemSpec.NonNullable(reader)

public infix fun <EB, O, CTX, T> ArrayItemSpec.NonNullable<EB, O, CTX, T>.validation(
    validator: Validator<EB, O, CTX, T>
): ArrayItemSpec.NonNullable<EB, O, CTX, T> =
    ArrayItemSpec.NonNullable(
        reader = { env, context, location, source ->
            reader.read(env, context, location, source).validation(env, context, validator)
        }
    )

public infix fun <EB, O, CTX, T> ArrayItemSpec.NonNullable<EB, O, CTX, T>.or(
    alt: ArrayItemSpec.NonNullable<EB, O, CTX, T>
): ArrayItemSpec.NonNullable<EB, O, CTX, T> =
    ArrayItemSpec.NonNullable(reader = reader or alt.reader)