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

import io.github.airflux.quickstart.domain.model.Tender
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderOptions
import io.github.airflux.quickstart.infrastructure.web.model.reader.property.identifierPropertySpec
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.CommonStructReaderValidators
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.reader.struct.property.specification.optional
import io.github.airflux.serialization.core.reader.struct.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.returns
import io.github.airflux.serialization.dsl.reader.struct.structReader

val TenderReader: JsReader<ReaderErrorBuilders, ReaderOptions, Tender> = structReader {
    validation(CommonStructReaderValidators)

    val id = property(identifierPropertySpec)
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    returns { _, location ->
        Tender(+id, +title, +value, +lots).toSuccess(location)
    }
}
