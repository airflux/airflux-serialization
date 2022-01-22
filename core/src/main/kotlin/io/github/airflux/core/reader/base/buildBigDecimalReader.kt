/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.value.extension.readAsNumber
import java.math.BigDecimal

/**
 * Reader for [BigDecimal] type.
 */
fun buildBigDecimalReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<BigDecimal> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            BigDecimal(text).asSuccess(location = p)
        }
    }
