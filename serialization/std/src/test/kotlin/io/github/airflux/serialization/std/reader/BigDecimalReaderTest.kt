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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigDecimal

internal class BigDecimalReaderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val BigDecimalReader = bigDecimalReader<EB, Unit>()
    }

    init {

        "The big decimal type reader" - {

            "should return the big decimal value" - {
                withData(
                    listOf("-10.5", "-10", "-0.5", "0", "0.5", "10", "10.5")
                ) { value ->
                    val source: JsValue = JsNumeric.Number.valueOrNullOf(value)!!
                    val result = BigDecimalReader.read(ENV, LOCATION, source)
                    result shouldBeSuccess success(location = LOCATION, value = BigDecimal(value))
                }
            }

            "should return the invalid type error" {
                val source: JsValue = JsString("abc")
                val result = BigDecimalReader.read(ENV, LOCATION, source)
                result shouldBeFailure failure(
                    location = JsLocation,
                    error = JsonErrors.InvalidType(
                        expected = listOf(JsNumeric.Number.nameOfType),
                        actual = JsString.nameOfType
                    )
                )
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
