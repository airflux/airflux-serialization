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

package io.github.airflux.core.value.extension

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue

public fun JsValue.readAsBoolean(context: JsReaderContext, location: JsLocation): JsResult<Boolean> =
    if (this is JsBoolean)
        JsResult.Success(location, this.get)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location = location, error = errorBuilder.build(JsValue.Type.BOOLEAN, this.type))
    }

public fun JsValue.readAsString(context: JsReaderContext, location: JsLocation): JsResult<String> =
    if (this is JsString)
        JsResult.Success(location, this.get)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location = location, error = errorBuilder.build(JsValue.Type.STRING, this.type))
    }

public fun <T : Number> JsValue.readAsNumber(
    context: JsReaderContext,
    location: JsLocation,
    reader: (JsReaderContext, JsLocation, String) -> JsResult<T>
): JsResult<T> =
    if (this is JsNumber)
        reader(context, location, this.get)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location = location, error = errorBuilder.build(JsValue.Type.NUMBER, this.type))
    }

public inline fun <T> JsValue.readAsObject(
    context: JsReaderContext,
    location: JsLocation,
    reader: (JsReaderContext, JsLocation, JsObject) -> JsResult<T>
): JsResult<T> =
    if (this is JsObject)
        reader(context, location, this)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location = location, error = errorBuilder.build(JsValue.Type.OBJECT, this.type))
    }

public inline fun <T> JsValue.readAsArray(
    context: JsReaderContext,
    location: JsLocation,
    reader: (JsReaderContext, JsLocation, JsArray<*>) -> JsResult<T>
): JsResult<T> =
    if (this is JsArray<*>)
        reader(context, location, this)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location = location, error = errorBuilder.build(JsValue.Type.ARRAY, this.type))
    }
