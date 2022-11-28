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

package io.github.airflux.quickstart.infrastructure.web.model.reader

import io.github.airflux.quickstart.infrastructure.web.model.reader.base.BigDecimalReader
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderCtx
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.std.validator.comparison.StdComparisonValidator
import java.math.BigDecimal

private val amountMoreZero: Validator<ReaderErrorBuilders, ReaderCtx, BigDecimal> =
    StdComparisonValidator.gt<ReaderErrorBuilders, ReaderCtx, BigDecimal>(BigDecimal.ZERO)

val AmountReader: Reader<ReaderErrorBuilders, ReaderCtx, BigDecimal> = BigDecimalReader.validation(amountMoreZero)
