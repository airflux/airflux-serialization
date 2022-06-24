package io.github.airflux.core.reader.`object`

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class RequiredFieldReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(
            listOf(
                PathMissingErrorBuilder { JsonErrors.PathMissing },
                InvalidTypeErrorBuilder(JsonErrors::InvalidType)
            )
        )
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            JsResult.Success(location, (input as JsString).get)
        }
    }

    init {

        "The readRequired function" - {

            "should return the result of applying the reader to a node if found it" {
                val from: JsLookup =
                    JsLookup.Defined(location = JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))

                val result: JsResult<String> = readRequired(context = CONTEXT, from = from, using = stringReader)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = USER_NAME_VALUE)
            }

            "should return the missing path error if did not find a node" {
                val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.empty.append("name"))

                val result: JsResult<String> = readRequired(context = CONTEXT, from = from, using = stringReader)

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

                val result: JsResult<String> = readRequired(context = CONTEXT, from = from, using = stringReader)

                result shouldBe JsResult.Failure(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            }
        }
    }
}
