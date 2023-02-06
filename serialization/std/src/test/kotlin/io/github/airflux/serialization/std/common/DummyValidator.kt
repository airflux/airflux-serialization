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

package io.github.airflux.serialization.std.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator

internal class DummyValidator<EB, O, CTX, T> private constructor(
    val result: (ReaderEnv<EB, O>, CTX, Location, T) -> ReaderResult.Failure?
) : Validator<EB, O, CTX, T> {

    override fun validate(env: ReaderEnv<EB, O>, context: CTX, location: Location, value: T): ReaderResult.Failure? =
        result(env, context, location, value)

    internal companion object {

        internal fun <EB, O, CTX> isNotEmptyString(error: () -> ReaderResult.Error): Validator<EB, O, CTX, String> =
            DummyValidator { _, _, location, value ->
                if (value.isNotEmpty())
                    null
                else
                    ReaderResult.Failure(location = location, error = error())
            }
    }
}
