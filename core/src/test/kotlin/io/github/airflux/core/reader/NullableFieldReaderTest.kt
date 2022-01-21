package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NullableFieldReaderTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            JsResult.Success((input as JsString).get, location)
        }
    }

    init {

        "The readNullable function" - {

            "should return the result of applying the reader to the node if it is not the JsNull node" {
                val from: JsLookup =
                    JsLookup.Defined(location = JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))

                val result: JsResult<String?> = readNullable(
                    from = from,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = USER_NAME_VALUE)
            }

            "should return a null value if found a JsNull node" {
                val from: JsLookup = JsLookup.Defined(location = JsLocation.empty.append("name"), JsNull)

                val result: JsResult<String?> = readNullable(
                    from = from,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = null)
            }

            "should return the missing path error if did not find a node" {
                val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.empty.append("name"))

                val result: JsResult<String?> = readNullable(
                    from = from,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Failure(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.PathMissing
                )
            }

            "should return the invalid type error if a node is not an object" {
                val from: JsLookup = JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty.append("name"),
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.STRING
                )

                val result: JsResult<String?> = readNullable(
                    from = from,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Failure(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            }
        }
    }
}
