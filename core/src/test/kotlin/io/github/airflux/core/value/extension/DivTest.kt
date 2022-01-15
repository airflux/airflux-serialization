package io.github.airflux.core.value.extension

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DivTest : FreeSpec() {

    companion object {
        private const val USER_NAME = "user"
    }

    init {
        "The 'div' function" - {
            "called with a parameter of a key path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))

                    val result = json / "name"

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / "name"

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }

                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))

                    val result = json / "user"

                    result as JsLookup.Undefined.PathMissing
                    result.location shouldBe JsLocation.Root / "user"
                }

                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME)

                    val result = json / "user"

                    result as JsLookup.Undefined.InvalidType
                    result.location shouldBe JsLocation.Root
                    result.expected shouldBe JsValue.Type.OBJECT
                    result.actual shouldBe JsValue.Type.STRING
                }
            }
            "called with a parameter of an idx path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsArray(JsString(USER_NAME))

                    val result = json / 0

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / 0

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }
                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found return" {
                    val json: JsValue = JsArray(JsString(USER_NAME))

                    val result = json / 1

                    result as JsLookup.Undefined.PathMissing
                    result.location shouldBe JsLocation.Root / 1
                }
                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME)

                    val result = json / 0

                    result as JsLookup.Undefined.InvalidType
                    result.location shouldBe JsLocation.Root
                    result.expected shouldBe JsValue.Type.ARRAY
                    result.actual shouldBe JsValue.Type.STRING
                }
            }
        }
    }
}
