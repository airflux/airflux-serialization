package io.github.airflux.dsl.value

import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsValueOpsTest : FreeSpec() {

    init {
        "The 'div' extension-function" - {
            "called with a parameter of a key path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                    val lookup = json / "name"

                    lookup shouldBe JsLookup.Defined(JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))
                }
                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                    val lookup = json / "user"

                    lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append("user"))
                }
                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME_VALUE)

                    val lookup = json / "user"

                    lookup shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty,
                        expected = JsValue.Type.OBJECT,
                        actual = JsValue.Type.STRING
                    )
                }
            }
            "called with a parameter of an idx path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsArray(JsString(USER_NAME_VALUE))

                    val lookup = json / 0

                    lookup shouldBe JsLookup.Defined(JsLocation.empty.append(0), JsString(USER_NAME_VALUE))
                }
                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found return" {
                    val json: JsValue = JsArray(JsString(USER_NAME_VALUE))

                    val lookup = json / 1

                    lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(1))
                }
                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME_VALUE)

                    val lookup = json / 0

                    lookup shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty,
                        expected = JsValue.Type.ARRAY,
                        actual = JsValue.Type.STRING
                    )
                }
            }
        }
    }
}
