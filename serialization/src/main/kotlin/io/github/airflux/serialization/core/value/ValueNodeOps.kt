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

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PathElement
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult

public fun ValueNode.readAsBoolean(context: ReaderContext, location: Location): JsResult<Boolean> =
    if (this is BooleanNode)
        JsResult.Success(location, this.get)
    else {
        val errorBuilder = context[InvalidTypeErrorBuilder]
        JsResult.Failure(location = location, error = errorBuilder.build(ValueNode.Type.BOOLEAN, this.type))
    }

public fun ValueNode.readAsString(context: ReaderContext, location: Location): JsResult<String> =
    if (this is StringNode)
        JsResult.Success(location, this.get)
    else {
        val errorBuilder = context[InvalidTypeErrorBuilder]
        JsResult.Failure(location = location, error = errorBuilder.build(ValueNode.Type.STRING, this.type))
    }

public fun <T : Number> ValueNode.readAsNumber(
    context: ReaderContext,
    location: Location,
    reader: (ReaderContext, Location, String) -> JsResult<T>
): JsResult<T> =
    if (this is NumberNode)
        reader(context, location, this.get)
    else {
        val errorBuilder = context[InvalidTypeErrorBuilder]
        JsResult.Failure(location = location, error = errorBuilder.build(ValueNode.Type.NUMBER, this.type))
    }

public inline fun <T> ValueNode.readAsObject(
    context: ReaderContext,
    location: Location,
    reader: (ReaderContext, Location, StructNode) -> JsResult<T>
): JsResult<T> =
    if (this is StructNode)
        reader(context, location, this)
    else {
        val errorBuilder = context[InvalidTypeErrorBuilder]
        JsResult.Failure(location = location, error = errorBuilder.build(ValueNode.Type.OBJECT, this.type))
    }

public inline fun <T> ValueNode.readAsArray(
    context: ReaderContext,
    location: Location,
    reader: (ReaderContext, Location, ArrayNode<*>) -> JsResult<T>
): JsResult<T> =
    if (this is ArrayNode<*>)
        reader(context, location, this)
    else {
        val errorBuilder = context[InvalidTypeErrorBuilder]
        JsResult.Failure(location = location, error = errorBuilder.build(ValueNode.Type.ARRAY, this.type))
    }

internal fun ValueNode.getOrNull(path: JsPath): ValueNode? {
    tailrec fun ValueNode.getOrNull(path: JsPath, idxElement: Int): ValueNode? {
        if (idxElement == path.elements.size) return this
        return when (val element = path.elements[idxElement]) {
            is PathElement.Key -> if (this is StructNode)
                this[element]?.getOrNull(path, idxElement + 1)
            else
                null

            is PathElement.Idx -> if (this is ArrayNode<*>)
                this[element]?.getOrNull(path, idxElement + 1)
            else
                null
        }
    }

    return this.getOrNull(path, 0)
}

internal operator fun StructNode.contains(path: JsPath): Boolean = this.getOrNull(path) != null
