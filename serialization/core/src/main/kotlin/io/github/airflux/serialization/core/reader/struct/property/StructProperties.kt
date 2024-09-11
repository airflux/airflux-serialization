/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.struct.property

import io.github.airflux.serialization.core.path.JsPath

public typealias StructProperties<EB, O> = List<StructProperty<EB, O, *>>

/**
 * Returns a set of start keys from the paths of each property.
 */
public fun <EB, O> StructProperties<EB, O>.startKeysOfPaths(): Collection<String> {
    fun JsPath.Element.getKeyOrNull(): String? = if (this is JsPath.Element.Key) get else null

    fun StructProperty<EB, O, *>.getStartKeysOfPaths(): List<String> = paths.items
        .mapNotNull { path -> path.head.getKeyOrNull() }

    return buildSet {
        for (property in this@startKeysOfPaths) {
            addAll(property.getStartKeysOfPaths())
        }
    }
}
