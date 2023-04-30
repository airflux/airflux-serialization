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
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success

public fun <EB, O> ValueNode.readAsBoolean(env: ReaderEnv<EB, O>, location: JsLocation): ReadingResult<Boolean>
    where EB : InvalidTypeErrorBuilder =
    if (this is BooleanNode)
        success(location = location, value = this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(BooleanNode.nameOfType), this.nameOfType)
        )

public fun <EB, O> ValueNode.readAsString(env: ReaderEnv<EB, O>, location: JsLocation): ReadingResult<String>
    where EB : InvalidTypeErrorBuilder =
    if (this is StringNode)
        success(location = location, value = this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(StringNode.nameOfType), this.nameOfType)
        )

public fun <EB, O, CTX, T : Number> ValueNode.readAsInteger(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (ReaderEnv<EB, O>, CTX, JsLocation, String) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is NumericNode.Integer)
        reader(env, context, location, this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(
                expected = listOf(NumericNode.Integer.nameOfType),
                actual = this.nameOfType
            )
        )

public fun <EB, O, CTX, T : Number> ValueNode.readAsNumber(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (ReaderEnv<EB, O>, CTX, JsLocation, String) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is NumericNode)
        reader(env, context, location, this.get)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(
                expected = listOf(NumericNode.Number.nameOfType),
                actual = this.nameOfType
            )
        )

public inline fun <EB, O, CTX, T> ValueNode.readAsStruct(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (ReaderEnv<EB, O>, CTX, JsLocation, StructNode) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is StructNode)
        reader(env, context, location, this)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(StructNode.nameOfType), this.nameOfType)
        )

public inline fun <EB, O, CTX, T> ValueNode.readAsArray(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: JsLocation,
    reader: (ReaderEnv<EB, O>, CTX, JsLocation, ArrayNode) -> ReadingResult<T>
): ReadingResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is ArrayNode)
        reader(env, context, location, this)
    else
        failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(ArrayNode.nameOfType), this.nameOfType)
        )

internal fun ValueNode.getOrNull(path: JsPath): ValueNode? {
    tailrec fun ValueNode.getOrNull(path: JsPath?): ValueNode? =
        if (path != null)
            when (val element = path.head) {
                is JsPath.Element.Key -> if (this is StructNode) this[element]?.getOrNull(path.tail) else null
                is JsPath.Element.Idx -> if (this is ArrayNode) this[element]?.getOrNull(path.tail) else null
            }
        else
            this

    return this.getOrNull(path)
}

internal operator fun StructNode.contains(path: JsPath): Boolean = this.getOrNull(path) != null
