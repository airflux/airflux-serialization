/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.core.reader.result.extension

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.JsResult

fun <T> JsResult<T?>.filter(predicate: JsPredicate<T>): JsResult<T?> =
    filter(context = JsReaderContext(), predicate = predicate)

fun <T> JsResult<T?>.filter(context: JsReaderContext, predicate: JsPredicate<T>): JsResult<T?> =
    when (this) {
        is JsResult.Success -> if (this.value != null) {
            if (predicate.test(context, this.location, this.value)) this
            else JsResult.Success(this.location, null)
        } else
            this

        is JsResult.Failure -> this
    }
