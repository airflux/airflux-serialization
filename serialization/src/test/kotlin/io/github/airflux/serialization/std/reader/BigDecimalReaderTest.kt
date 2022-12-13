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

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.assertAsFailure
import io.github.airflux.serialization.common.assertAsSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigDecimal

internal class BigDecimalReaderTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private val BigDecimalReader = bigDecimalReader<EB, Unit>()
    }

    init {

        "The big decimal type reader" - {

            "should return the big decimal value" - {
                withData(
                    listOf("-10.5", "-10", "-0.5", "0", "0.5", "10", "10.5")
                ) { value ->
                    val source: ValueNode = NumberNode.valueOf(value)!!
                    val result = BigDecimalReader.read(ENV, LOCATION, source)
                    result.assertAsSuccess(value = BigDecimal(value))
                }
            }

            "should return the invalid type error" {
                val source: ValueNode = StringNode("abc")
                val result = BigDecimalReader.read(ENV, LOCATION, source)
                result.assertAsFailure(
                    ReaderResult.Failure.Cause(
                        location = Location.empty,
                        error = JsonErrors.InvalidType(expected = listOf(ValueNode.Type.NUMBER), actual = ValueNode.Type.STRING)
                    )
                )
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<ValueNode.Type>, actual: ValueNode.Type): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
