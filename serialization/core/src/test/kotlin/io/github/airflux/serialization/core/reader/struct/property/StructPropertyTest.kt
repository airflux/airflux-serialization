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

package io.github.airflux.serialization.core.reader.struct.property

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsPathReader
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyPathReader
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructPropertyTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val ENV = JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = Unit))
        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
        private val READER: JsPathReader<EB, Unit, String> = DummyPathReader { env, location, source ->
            StringReader.read(env, location, source)
        }
    }

    init {

        "The StructProperty type" - {
            val spec = StructPropertySpec(paths = JsPaths(JsPath(PROPERTY_NAME)), reader = READER)
            val property = JsStructProperty(spec)

            "then the paths should equal the paths from the spec" {
                property.paths shouldBe spec.paths
            }

            "then the method read should return the value" {
                val source = JsString(PROPERTY_VALUE)
                val result = property.read(ENV, LOCATION, source)
                result.shouldBeSuccess(location = LOCATION, value = PROPERTY_VALUE)
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }
}
