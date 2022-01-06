package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class LookupTest : FreeSpec() {

    companion object {
        private const val USER_NAME = "user"
    }

    init {
        "The 'lookup' function" - {
            "when called with a parameter of a simple path" - {
                "should return the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val path: JsPath.Identifiable = JsPath.Root / "name"

                    val result = json.lookup(JsLocation.Root, path)

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / "name"

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }
                "should return the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val path: JsPath.Identifiable = JsPath.Root / "user"

                    val result = json.lookup(JsLocation.Root, path)

                    result as JsLookup.Undefined.PathMissing
                    result.location shouldBe JsLocation.Root / "user"
                }
            }
            "when called with a parameter of a composite path" - {
                "should return the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject(
                        "user" to JsObject(
                            "name" to JsString(USER_NAME)
                        )
                    )
                    val path: JsPath.Identifiable = JsPath.Root / "user" / "name"

                    val result = json.lookup(JsLocation.Root, path)

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / "user" / "name"

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }
                "should return the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject(
                        "user" to JsObject(
                            "name" to JsString(USER_NAME)
                        )
                    )
                    val path: JsPath.Identifiable = JsPath.Root / "user" / "phones" / 0

                    val result = json.lookup(JsLocation.Root, path)

                    result as JsLookup.Undefined
                    result.location shouldBe JsLocation.Root / "user" / "phones"
                }
            }
            "when called with a parameter of a key path element" - {
                "should return the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val pathElement: PathElement = KeyPathElement("name")

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / "name"

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }
                "should return the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val pathElement: PathElement = KeyPathElement("user")

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Undefined.PathMissing
                    result.location shouldBe JsLocation.Root / "user"
                }
                "should return the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME)
                    val pathElement: PathElement = KeyPathElement("user")

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Undefined.InvalidType
                    result.location shouldBe JsLocation.Root
                    result.expected shouldBe JsValue.Type.OBJECT
                    result.actual shouldBe JsValue.Type.STRING
                }
            }
            "when called with a parameter of an idx path element" - {
                "should return the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsArray(JsString(USER_NAME))
                    val pathElement: PathElement = IdxPathElement(0)

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Defined
                    result.location shouldBe JsLocation.Root / 0

                    val value = result.value as JsString
                    value.get shouldBe USER_NAME
                }
                "should return the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsArray(JsString(USER_NAME))
                    val pathElement: PathElement = IdxPathElement(1)

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Undefined.PathMissing
                    result.location shouldBe JsLocation.Root / 1
                }
                "should return the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME)
                    val pathElement: PathElement = IdxPathElement(0)

                    val result = json.lookup(JsLocation.Root, pathElement)

                    result as JsLookup.Undefined.InvalidType
                    result.location shouldBe JsLocation.Root
                    result.expected shouldBe JsValue.Type.ARRAY
                    result.actual shouldBe JsValue.Type.STRING
                }
            }
        }
    }
}
