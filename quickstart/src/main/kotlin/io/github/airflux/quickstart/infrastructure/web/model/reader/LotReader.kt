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

import io.github.airflux.quickstart.domain.model.Lot
import io.github.airflux.quickstart.domain.model.LotStatus
import io.github.airflux.quickstart.infrastructure.web.model.reader.base.StringReader
import io.github.airflux.quickstart.infrastructure.web.model.reader.base.asEnum
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderOptions
import io.github.airflux.quickstart.infrastructure.web.model.reader.property.identifierPropertySpec
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.CommonStructReaderValidators
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.additionalProperties
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.isNotBlank
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.reader.struct.property.specification.required
import io.github.airflux.serialization.core.reader.struct.validation.and
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.dsl.reader.struct.returns
import io.github.airflux.serialization.dsl.reader.struct.structReader

val LotStatusReader: JsReader<ReaderErrorBuilders, ReaderOptions, LotStatus> =
    StringReader.validation(isNotBlank).asEnum()

val LotReader: JsReader<ReaderErrorBuilders, ReaderOptions, Lot> = structReader {
    validation { properties -> CommonStructReaderValidators and additionalProperties(properties) }

    val id = property(identifierPropertySpec)
    val status = property(required(name = "status", reader = LotStatusReader))
    val value = property(required(name = "value", reader = ValueReader))

    returns { _, location ->
        Lot(id = +id, status = +status, value = +value).toSuccess(location)
    }
}
