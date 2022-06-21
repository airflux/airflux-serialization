package io.github.airflux.core.reader.`object`

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.DEFAULT_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class NullableWithDefaultFieldReaderTest : FreeSpec() {

    companion object {
        private val context: JsReaderContext = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            JsResult.Success(location, (input as JsString).get)
        }
        private val defaultValue = { DEFAULT_VALUE }
    }

    init {

        "The readNullable (with default) function" - {

            "should return the result of applying the reader to the node if it is not the JsNull node" {
                val from: JsLookup =
                    JsLookup.Defined(location = JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))

                val result: JsResult<String?> =
                    readNullable(context = context, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = USER_NAME_VALUE)
            }

            "should return a null value if found a JsNull node" {
                val from: JsLookup = JsLookup.Defined(location = JsLocation.empty.append("name"), JsNull)

                val result: JsResult<String?> =
                    readNullable(context = context, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = null)
            }

            "should return the default value if did not find a node" {
                val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.empty.append("name"))

                val result: JsResult<String?> =
                    readNullable(context = context, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = DEFAULT_VALUE)
            }

            "should return the invalid type error if a node is not an object" {
                val from: JsLookup = JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty.append("name"),
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.STRING
                )

                val result: JsResult<String?> =
                    readNullable(context = context, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Failure(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            }
        }
    }
}
