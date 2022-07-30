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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.context.option.ContextOptionElement
import io.github.airflux.serialization.core.context.option.ContextOptionKey
import io.github.airflux.serialization.core.context.option.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.context.WriterContext
import java.math.BigDecimal

internal val WriterContext.stripTrailingZeros: Boolean
    get() = get(BigDecimalWriter.StripTrailingZeros) { false }

/**
 * Writer for primitive [BigDecimal] type.
 */
public object BigDecimalWriter : Writer<BigDecimal> {

    override fun write(context: WriterContext, location: Location, value: BigDecimal): ValueNode {
        val text = if (context.stripTrailingZeros) value.stripTrailingZeros().toPlainString() else value.toPlainString()
        return NumberNode.valueOf(text)!!
    }

    public class StripTrailingZeros(override val value: Boolean) : ContextOptionElement<Boolean> {
        override val key: ContextOptionKey<Boolean, *> = Key

        public companion object Key : ContextOptionKey<Boolean, StripTrailingZeros>
    }
}
