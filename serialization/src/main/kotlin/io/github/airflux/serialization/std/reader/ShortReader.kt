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

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.value.readAsNumber

/**
 * Reader for primitive [Short] type.
 */
public object ShortReader : Reader<Short> {
    override fun read(context: ReaderContext, location: Location, source: ValueNode): ReaderResult<Short> =
        source.readAsNumber(context, location) { ctx, l, value ->
            try {
                value.toShort().success(location = l)
            } catch (expected: NumberFormatException) {
                val errorBuilder = ctx[ValueCastErrorBuilder]
                errorBuilder.build(value, Short::class).failure(location = l)
            }
        }
}
