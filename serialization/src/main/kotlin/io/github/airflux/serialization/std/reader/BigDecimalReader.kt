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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.value.readAsNumber
import java.math.BigDecimal

/**
 * Reader for [BigDecimal] type.
 */
public object BigDecimalReader : Reader<BigDecimal> {
    override fun read(context: ReaderContext, location: Location, input: ValueNode): JsResult<BigDecimal> =
        input.readAsNumber(context, location) { _, p, text ->
            BigDecimal(text).success(location = p)
        }
}
