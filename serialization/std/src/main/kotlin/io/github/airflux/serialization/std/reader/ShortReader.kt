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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.NumberFormatErrorBuilder

/**
 * Reader for primitive [Short] type.
 */
public fun <EB, O> shortReader(): JsReader<EB, O, Short>
    where EB : InvalidTypeErrorBuilder,
          EB : NumberFormatErrorBuilder =
    JsReader { env, location, source ->
        source.tryConvertToNumber(env, location, String::toShort)
    }
