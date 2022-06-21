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

import io.github.airflux.core.context.option.JsContextOptionElement
import io.github.airflux.core.context.option.JsContextOptionKey
import io.github.airflux.core.context.option.get
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.context.JsWriterContext
import java.math.BigDecimal

internal val JsWriterContext.stripTrailingZeros: Boolean
    get() = get(BigDecimalWriter.StripTrailingZeros) { false }

/**
 * Writer for primitive [BigDecimal] type.
 */
public object BigDecimalWriter : JsWriter<BigDecimal> {

    override fun write(context: JsWriterContext, location: JsLocation, value: BigDecimal): JsValue? {
        val text = if (context.stripTrailingZeros) value.stripTrailingZeros().toPlainString() else value.toPlainString()
        return JsNumber.valueOf(text)!!
    }

    public class StripTrailingZeros(override val value: Boolean) : JsContextOptionElement<Boolean> {
        override val key: JsContextOptionKey<Boolean, *> = Key

        public companion object Key : JsContextOptionKey<Boolean, StripTrailingZeros>
    }
}
