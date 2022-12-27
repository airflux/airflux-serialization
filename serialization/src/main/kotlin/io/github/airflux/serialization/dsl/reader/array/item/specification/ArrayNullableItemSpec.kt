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

package io.github.airflux.serialization.dsl.reader.array.item.specification

import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.core.value.NullNode

public fun <EB, CTX, T : Any> nullable(reader: Reader<EB, CTX, T>): ArrayItemSpec.Nullable<EB, CTX, T?> =
    ArrayItemSpec.Nullable(
        reader = { env, location, source ->
            if (source is NullNode)
                ReaderResult.Success(location = location, value = null)
            else
                reader.read(env, location, source)
        }
    )

public infix fun <EB, CTX, T> ArrayItemSpec.Nullable<EB, CTX, T>.validation(
    validator: Validator<EB, CTX, T?>
): ArrayItemSpec.Nullable<EB, CTX, T> =
    ArrayItemSpec.Nullable(
        reader = { env, location, source ->
            reader.read(env, location, source).validation(env, validator)
        }
    )

public infix fun <EB, CTX, T> ArrayItemSpec.Nullable<EB, CTX, T>.or(
    alt: ArrayItemSpec.Nullable<EB, CTX, T>
): ArrayItemSpec.Nullable<EB, CTX, T> =
    ArrayItemSpec.Nullable(reader = reader or alt.reader)