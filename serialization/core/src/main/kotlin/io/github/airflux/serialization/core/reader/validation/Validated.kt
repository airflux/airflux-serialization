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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.result.ReaderResult

public sealed class Validated {
    public object Valid : Validated()
    public class Invalid(public val reason: ReaderResult.Failure) : Validated()
}

public inline fun <T> Validated.fold(ifInvalid: (ReaderResult.Failure) -> T, ifValid: () -> T): T =
    when (this) {
        is Validated.Valid -> ifValid()
        is Validated.Invalid -> ifInvalid(this.reason)
    }

public inline fun Validated.ifInvalid(handler: (ReaderResult.Failure) -> Unit) {
    if (this is Validated.Invalid) handler(this.reason)
}

public fun valid(): Validated = Validated.Valid

public fun invalid(location: Location, error: ReaderResult.Error): Validated =
    ReaderResult.Failure(location, error).toInvalid()

public fun ReaderResult.Failure.toInvalid(): Validated = Validated.Invalid(this)
