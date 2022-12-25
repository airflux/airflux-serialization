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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult

public fun <EB, CTX> ValueNode.readAsBoolean(env: ReaderEnv<EB, CTX>, location: Location): ReaderResult<Boolean>
    where EB : InvalidTypeErrorBuilder =
    if (this is BooleanNode)
        ReaderResult.Success(location = location, value = this.get)
    else
        ReaderResult.Failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(BooleanNode.nameOfType), this.nameOfType)
        )

public fun <EB, CTX> ValueNode.readAsString(env: ReaderEnv<EB, CTX>, location: Location): ReaderResult<String>
    where EB : InvalidTypeErrorBuilder =
    if (this is StringNode)
        ReaderResult.Success(location = location, value = this.get)
    else
        ReaderResult.Failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(StringNode.nameOfType), this.nameOfType)
        )

public fun <EB, CTX, T : Number> ValueNode.readAsNumber(
    env: ReaderEnv<EB, CTX>,
    location: Location,
    reader: (ReaderEnv<EB, CTX>, Location, String) -> ReaderResult<T>
): ReaderResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is NumericNode)
        reader(env, location, this.get)
    else
        ReaderResult.Failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(NumericNode.nameOfType), this.nameOfType)
        )

public inline fun <EB, CTX, T> ValueNode.readAsStruct(
    env: ReaderEnv<EB, CTX>,
    location: Location,
    reader: (ReaderEnv<EB, CTX>, Location, StructNode) -> ReaderResult<T>
): ReaderResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is StructNode)
        reader(env, location, this)
    else
        ReaderResult.Failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(StructNode.nameOfType), this.nameOfType)
        )

public inline fun <EB, CTX, T> ValueNode.readAsArray(
    env: ReaderEnv<EB, CTX>,
    location: Location,
    reader: (ReaderEnv<EB, CTX>, Location, ArrayNode<*>) -> ReaderResult<T>
): ReaderResult<T>
    where EB : InvalidTypeErrorBuilder =
    if (this is ArrayNode<*>)
        reader(env, location, this)
    else
        ReaderResult.Failure(
            location = location,
            error = env.errorBuilders.invalidTypeError(listOf(ArrayNode.nameOfType), this.nameOfType)
        )

internal fun ValueNode.getOrNull(path: PropertyPath): ValueNode? {
    tailrec fun ValueNode.getOrNull(path: PropertyPath, idxElement: Int): ValueNode? {
        if (idxElement == path.elements.size) return this
        return when (val element = path.elements[idxElement]) {
            is PropertyPath.Element.Key -> if (this is StructNode)
                this[element]?.getOrNull(path, idxElement + 1)
            else
                null

            is PropertyPath.Element.Idx -> if (this is ArrayNode<*>)
                this[element]?.getOrNull(path, idxElement + 1)
            else
                null
        }
    }

    return this.getOrNull(path, 0)
}

internal operator fun StructNode.contains(path: PropertyPath): Boolean = this.getOrNull(path) != null
