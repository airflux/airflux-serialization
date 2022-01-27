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

package io.github.airflux.dsl.reader.`object`.property.specification.builder

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.dsl.reader.`object`.property.specification.JsOptionalWithDefaultReaderPropertySpec

@Suppress("unused")
fun <T : Any> optionalWithDefault(name: String, reader: JsReader<T>, default: () -> T) =
    optionalWithDefault(JsPath(name), reader, default)

fun <T : Any> optionalWithDefault(path: JsPath, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.OptionalWithDefault { invalidTypeErrorBuilder ->
        JsOptionalWithDefaultReaderPropertySpec(path, reader, default, invalidTypeErrorBuilder)
    }