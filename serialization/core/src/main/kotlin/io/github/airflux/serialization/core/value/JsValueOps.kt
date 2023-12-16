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

import io.github.airflux.serialization.core.path.JsPath

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
