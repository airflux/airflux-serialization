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

package io.github.airflux.quickstart.infrastructure.web.model.reader

import io.github.airflux.quickstart.domain.model.Lots
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderOptions
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.CommonArrayReaderValidators
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.bind
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.dsl.reader.array.arrayReader
import io.github.airflux.serialization.dsl.reader.array.returns

val LotsReader: JsReader<ReaderErrorBuilders, ReaderOptions, Lots> = arrayReader {
    validation(CommonArrayReaderValidators)
    returns(items = LotReader)
}.bind { _, result ->
    Lots(result.value).toSuccess(result.location)
}
