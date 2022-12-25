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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ByteWriterTest : FreeSpec() {

    companion object {
        private val ENV = WriterEnv(context = Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The byte type writer" - {
            val writer: Writer<Unit, Byte> = byteWriter()
            val value: Byte = Byte.MAX_VALUE

            "should return the NumericNode value" {
                val result = writer.write(ENV, LOCATION, value)
                result shouldBe NumericNode.Integer.valueOf(value)
            }
        }
    }
}
