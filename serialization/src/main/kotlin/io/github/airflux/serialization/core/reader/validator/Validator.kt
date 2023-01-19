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

package io.github.airflux.serialization.core.reader.validator

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult

public fun interface Validator<EB, O, CTX, in T> {
    public fun validate(env: ReaderEnv<EB, O, CTX>, location: Location, value: T): ReaderResult.Failure?
}

/*
 * | This | Other  | Result |
 * |------|--------|--------|
 * | S    | ignore | S      |
 * | F    | S      | S      |
 * | F    | F`     | F + F` |
 */
public infix fun <EB, O, CTX, T> Validator<EB, O, CTX, T>.or(
    other: Validator<EB, O, CTX, T>
): Validator<EB, O, CTX, T> {
    val self = this
    return Validator { env, location, value ->
        self.validate(env, location, value)
            ?.let { error ->
                other.validate(env, location, value)
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
public infix fun <EB, O, CTX, T> Validator<EB, O, CTX, T>.and(
    other: Validator<EB, O, CTX, T>
): Validator<EB, O, CTX, T> {
    val self = this
    return Validator { env, location, value ->
        val result = self.validate(env, location, value)
        result ?: other.validate(env, location, value)
    }
}
