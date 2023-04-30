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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success

public fun <EB, O> JsValue.readAsBoolean(env: JsReaderEnv<EB, O>, location: JsLocation): ReadingResult<Boolean>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsBoolean)
        success(location = location, value = this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(JsBoolean.nameOfType), this.nameOfType)
        )

public fun <EB, O> JsValue.readAsString(env: JsReaderEnv<EB, O>, location: JsLocation): ReadingResult<String>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsString)
        success(location = location, value = this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(JsString.nameOfType), this.nameOfType)
        )

public fun <EB, O, CTX, T : Number> JsValue.readAsInteger(
    env: JsReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (JsReaderEnv<EB, O>, CTX, JsLocation, String) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsNumeric.Integer)
        reader(env, context, location, this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(
                expected = listOf(JsNumeric.Integer.nameOfType),
                actual = this.nameOfType
            )
        )

public fun <EB, O, CTX, T : Number> JsValue.readAsNumber(
    env: JsReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (JsReaderEnv<EB, O>, CTX, JsLocation, String) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsNumeric)
        reader(env, context, location, this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(
                expected = listOf(JsNumeric.Number.nameOfType),
                actual = this.nameOfType
            )
        )

public inline fun <EB, O, CTX, T> JsValue.readAsStruct(
    env: JsReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (JsReaderEnv<EB, O>, CTX, JsLocation, JsStruct) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsStruct)
        reader(env, context, location, this)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(JsStruct.nameOfType), this.nameOfType)
        )

public inline fun <EB, O, CTX, T> JsValue.readAsArray(
    env: JsReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (JsReaderEnv<EB, O>, CTX, JsLocation, JsArray) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is JsArray)
        reader(env, context, location, this)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(JsArray.nameOfType), this.nameOfType)
        )

internal fun JsValue.getOrNull(path: JsPath): JsValue? {
    tailrec fun JsValue.getOrNull(path: JsPath?): JsValue? =
        if (path != null)
            when (val element = path.head) {
                is JsPath.Element.Key -> if (this is JsStruct) this[element]?.getOrNull(path.tail) else null
                is JsPath.Element.Idx -> if (this is JsArray) this[element]?.getOrNull(path.tail) else null
            }
        else
            this

    return this.getOrNull(path)
}

internal operator fun JsStruct.contains(path: JsPath): Boolean = this.getOrNull(path) != null
