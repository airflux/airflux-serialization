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

package io.github.airflux.dsl.reader.`object`.property.specification

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

public fun <T : Any> required(name: String, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    required(JsPath(name), reader)

public fun <T : Any> required(path: JsPath, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    JsObjectRequiredPropertySpec.of(path, reader)

public fun <T : Any> required(paths: JsPaths, reader: JsReader<T>): JsObjectPropertySpec.Required<T> =
    JsObjectRequiredPropertySpec.of(paths, reader)

public fun <T : Any> defaultable(
    name: String,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.Defaultable<T> =
    defaultable(JsPath(name), reader, default)

public fun <T : Any> defaultable(
    path: JsPath,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.Defaultable<T> =
    JsObjectDefaultablePropertySpec.of(path, reader, default)

public fun <T : Any> defaultable(
    paths: JsPaths,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.Defaultable<T> =
    JsObjectDefaultablePropertySpec.of(paths, reader, default)

public fun <T : Any> optional(name: String, reader: JsReader<T>): JsObjectPropertySpec.Optional<T> =
    optional(JsPath(name), reader)

public fun <T : Any> optional(path: JsPath, reader: JsReader<T>): JsObjectPropertySpec.Optional<T> =
    JsObjectOptionalPropertySpec.of(path, reader)

public fun <T : Any> optional(paths: JsPaths, reader: JsReader<T>): JsObjectPropertySpec.Optional<T> =
    JsObjectOptionalPropertySpec.of(paths, reader)

public fun <T : Any> optionalWithDefault(
    name: String,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    optionalWithDefault(JsPath(name), reader, default)

public fun <T : Any> optionalWithDefault(
    path: JsPath,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectOptionalWithDefaultPropertySpec.of(path, reader, default)

public fun <T : Any> optionalWithDefault(
    paths: JsPaths,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectOptionalWithDefaultPropertySpec.of(paths, reader, default)

public fun <T : Any> nullable(name: String, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    nullable(JsPath(name), reader)

public fun <T : Any> nullable(path: JsPath, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    JsObjectNullablePropertySpec.of(path, reader)

public fun <T : Any> nullable(paths: JsPaths, reader: JsReader<T>): JsObjectPropertySpec.Nullable<T> =
    JsObjectNullablePropertySpec.of(paths, reader)

public fun <T : Any> nullableWithDefault(
    name: String,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.NullableWithDefault<T> =
    nullableWithDefault(JsPath(name), reader, default)

public fun <T : Any> nullableWithDefault(
    path: JsPath,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.NullableWithDefault<T> =
    JsObjectNullableWithDefaultPropertySpec.of(path, reader, default)

public fun <T : Any> nullableWithDefault(
    paths: JsPaths,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.NullableWithDefault<T> =
    JsObjectNullableWithDefaultPropertySpec.of(paths, reader, default)
