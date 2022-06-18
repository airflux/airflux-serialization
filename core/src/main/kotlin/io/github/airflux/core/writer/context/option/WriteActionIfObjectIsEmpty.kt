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

package io.github.airflux.core.writer.context.option

import io.github.airflux.core.writer.context.JsWriterAbstractContextElement
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.contextKeyName

public val JsWriterContext.writeActionIfObjectIsEmpty: WriteActionIfObjectIsEmpty.Action
    get() = getOrNull(WriteActionIfObjectIsEmpty)?.action ?: WriteActionIfObjectIsEmpty.Action.SKIP

public class WriteActionIfObjectIsEmpty(public val action: Action) :
    JsWriterAbstractContextElement<WriteActionIfObjectIsEmpty>(key = WriteActionIfObjectIsEmpty) {

    public enum class Action { EMPTY, NULL, SKIP }

    public companion object Key : JsWriterContext.Key<WriteActionIfObjectIsEmpty> {
        override val name: String = contextKeyName()
    }
}
