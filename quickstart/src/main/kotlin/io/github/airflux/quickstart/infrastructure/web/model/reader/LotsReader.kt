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

package io.github.airflux.quickstart.infrastructure.web.model.reader

import io.github.airflux.quickstart.domain.model.Lots
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderCtx
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderOptions
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.CommonArrayReaderValidators
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.flatMapResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.dsl.reader.array.arrayReader
import io.github.airflux.serialization.dsl.reader.array.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.returns

val LotsReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, Lots> = arrayReader {
    validation(CommonArrayReaderValidators)
    returns(items = nonNullable(LotReader))
}.flatMapResult { env, _, location, items ->
    withCatching(env, location) {
        Lots(items).success(location)
    }
}
