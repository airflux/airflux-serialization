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

package io.github.airflux.serialization.dsl.common

import io.github.airflux.serialization.core.reader.result.ReaderResult

internal sealed class JsonErrors : ReaderResult.Error {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: Iterable<String>, val actual: String) : JsonErrors()

    object AdditionalItems : JsonErrors()

    sealed class Validation : JsonErrors() {

        sealed class Struct : Validation() {
            object AdditionalProperties : Struct()
        }

        sealed class Arrays : Validation() {
            data class MinItems(val expected: Int, val actual: Int) : Arrays()
        }

        sealed class Numbers : Validation() {
            data class Ne(val expected: Number, val actual: Number) : Numbers()
        }

        sealed class Strings : Validation() {
            object IsEmpty : Strings()
        }
    }
}