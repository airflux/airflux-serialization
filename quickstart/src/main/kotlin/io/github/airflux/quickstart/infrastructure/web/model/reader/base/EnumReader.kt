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

package io.github.airflux.quickstart.infrastructure.web.model.reader.base

import io.github.airflux.quickstart.infrastructure.web.error.JsonErrors
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.bind
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.toSuccess

inline fun <EB, O, CTX, reified T : Enum<T>> JsReader<EB, O, CTX, String>.asEnum(): JsReader<EB, O, CTX, T> =
    bind { _, _, value ->
        try {
            enumValueOf<T>(value.value.uppercase()).toSuccess(value.location)
        } catch (ignored: Exception) {
            val allowable = enumValues<T>()
            failure(
                location = value.location,
                error = JsonErrors.EnumCast(actual = value.value, expected = allowable.joinToString())
            )
        }
    }
