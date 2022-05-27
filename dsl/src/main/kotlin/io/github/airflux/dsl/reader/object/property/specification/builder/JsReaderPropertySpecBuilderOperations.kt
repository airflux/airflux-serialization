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

@file:Suppress("unused", "TooManyFunctions")

package io.github.airflux.dsl.reader.`object`.property.specification.builder

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths
import io.github.airflux.dsl.reader.`object`.property.specification.JsDefaultableReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.JsNullableReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.JsNullableWithDefaultReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.JsOptionalReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.JsOptionalWithDefaultReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.JsRequiredReaderPropertySpec

fun <T : Any> required(name: String, reader: JsReader<T>) =
    required(JsPath(name), reader)

fun <T : Any> required(path: JsPath, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Required {
        JsRequiredReaderPropertySpec.of(path, reader)
    }

fun <T : Any> required(paths: JsPaths, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Required {
        JsRequiredReaderPropertySpec.of(paths, reader)
    }

fun <T : Any> defaultable(name: String, reader: JsReader<T>, default: () -> T) =
    defaultable(JsPath(name), reader, default)

fun <T : Any> defaultable(path: JsPath, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.Defaultable {
        JsDefaultableReaderPropertySpec.of(path, reader, default)
    }

fun <T : Any> defaultable(paths: JsPaths, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.Defaultable {
        JsDefaultableReaderPropertySpec.of(paths, reader, default)
    }

fun <T : Any> optional(name: String, reader: JsReader<T>) =
    optional(JsPath(name), reader)

fun <T : Any> optional(path: JsPath, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Optional {
        JsOptionalReaderPropertySpec.of(path, reader)
    }

fun <T : Any> optional(paths: JsPaths, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Optional {
        JsOptionalReaderPropertySpec.of(paths, reader)
    }

fun <T : Any> optionalWithDefault(name: String, reader: JsReader<T>, default: () -> T) =
    optionalWithDefault(JsPath(name), reader, default)

fun <T : Any> optionalWithDefault(path: JsPath, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.OptionalWithDefault {
        JsOptionalWithDefaultReaderPropertySpec.of(path, reader, default)
    }

fun <T : Any> optionalWithDefault(paths: JsPaths, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.OptionalWithDefault {
        JsOptionalWithDefaultReaderPropertySpec.of(paths, reader, default)
    }

fun <T : Any> nullable(name: String, reader: JsReader<T>) =
    nullable(JsPath(name), reader)

fun <T : Any> nullable(path: JsPath, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Nullable {
        JsNullableReaderPropertySpec.of(path, reader)
    }

fun <T : Any> nullable(paths: JsPaths, reader: JsReader<T>) =
    JsReaderPropertySpecBuilder.Nullable {
        JsNullableReaderPropertySpec.of(paths, reader)
    }

fun <T : Any> nullableWithDefault(name: String, reader: JsReader<T>, default: () -> T) =
    nullableWithDefault(JsPath(name), reader, default)

fun <T : Any> nullableWithDefault(path: JsPath, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.NullableWithDefault {
        JsNullableWithDefaultReaderPropertySpec.of(path, reader, default)
    }

fun <T : Any> nullableWithDefault(paths: JsPaths, reader: JsReader<T>, default: () -> T) =
    JsReaderPropertySpecBuilder.NullableWithDefault {
        JsNullableWithDefaultReaderPropertySpec.of(paths, reader, default)
    }
