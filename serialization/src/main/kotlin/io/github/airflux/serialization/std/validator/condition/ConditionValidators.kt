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

package io.github.airflux.serialization.std.validator.condition

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <EB, O, CTX, T> Validator<EB, O, CTX, T>.applyIfNotNull(): Validator<EB, O, CTX, T?> =
    Validator { env, location, value ->
        if (value != null) validate(env, location, value) else null
    }

public fun <EB, O, CTX, T> Validator<EB, O, CTX, T>.applyIf(
    predicate: (ReaderEnv<EB, O, CTX>, Location, T) -> Boolean
): Validator<EB, O, CTX, T> =
    Validator { env, location, value ->
        if (predicate(env, location, value)) validate(env, location, value) else null
    }
