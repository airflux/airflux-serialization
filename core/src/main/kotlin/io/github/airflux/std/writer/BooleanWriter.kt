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

package io.github.airflux.std.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.context.JsWriterContext

/**
 * Writer for primitive [Boolean] type.
 */
public object BooleanWriter : JsWriter<Boolean> {
    override fun write(context: JsWriterContext, location: JsLocation, value: Boolean): JsValue =
        if (value) JsBoolean.True else JsBoolean.False
}
