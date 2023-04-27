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

package io.github.airflux.serialization.dsl.reader.struct.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.dsl.common.DummyReader
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructPropertyTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val ENV = ReaderEnv(errorBuilders = EB(), options = Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location

        private val StringReader: Reader<EB, Unit, Unit, String> = DummyReader.string()
    }

    init {

        "The StructProperty type" - {
            val spec = StructPropertySpec(paths = PropertyPaths(PropertyPath(PROPERTY_NAME)), reader = StringReader)
            val property = StructProperty(spec)

            "then the paths should equal the paths from the spec" {
                property.paths shouldBe spec.paths
            }

            "then the method read should return the value" {
                val source = StringNode(PROPERTY_VALUE)
                val result = property.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeSuccess success(location = LOCATION, value = PROPERTY_VALUE)
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
    }
}
