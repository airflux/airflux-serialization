package io.github.airflux.core.lookup

import io.github.airflux.core.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertEquals

class JsLookupTest : FreeSpec() {

    init {

        "JsLookup#apply(_, PathElement.Key, _)" - {

            "a key is found" {
                val node = JsObject("name" to JsString(USER_NAME_VALUE))
                val key = "name"

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Defined(JsLocation.Root / key, JsString(USER_NAME_VALUE))
            }

            "a key is not found" {
                val node = JsObject("name" to JsString(USER_NAME_VALUE))
                val key = "user"

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / key)
            }

            "a node is invalid type" {
                val node = JsString(USER_NAME_VALUE)
                val key = "user"

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.Root,
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.STRING
                )
            }
        }

        "JsLookup#apply(_, PathElement.Idx, _)" - {

            "an idx is found" {
                val node = JsArray(JsString(FIRST_PHONE_VALUE))
                val idx = 0

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Defined(JsLocation.Root / idx, JsString(FIRST_PHONE_VALUE))
            }

            "an idx is not found" {
                val node = JsArray(JsString(FIRST_PHONE_VALUE))
                val idx = 1

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / idx)
            }

            "a node is invalid type" {
                val node = JsString(USER_NAME_VALUE)
                val idx = 0

                val lookup = JsLookup.apply(JsLocation.Root, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.Root,
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.STRING
                )
            }
        }

        "JsLookup#apply(_, JsPath, _)" - {

            val node = JsObject(
                "user" to JsObject(
                    "name" to JsString(USER_NAME_VALUE),
                    "phones" to JsArray(JsString(FIRST_PHONE_VALUE))
                )
            )

            "keyed element" - {

                "a key is found" {
                    val path = JsPath("user").append("name")

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Defined(JsLocation.Root / "user" / "name", JsString(USER_NAME_VALUE))
                }

                "a key is not found" {
                    val path = JsPath("user").append("id")

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / "user" / "id")
                }

                "a node is invalid type" {
                    val path = JsPath("user").append("phones").append("mobile")

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.Root / "user" / "phones",
                        expected = JsValue.Type.OBJECT,
                        actual = JsValue.Type.ARRAY
                    )
                }
            }

            "indexed element" - {

                "an idx is found" {
                    val path = JsPath("user").append("phones").append(0)

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Defined(
                        JsLocation.Root / "user" / "phones" / 0,
                        JsString(FIRST_PHONE_VALUE)
                    )
                }

                "an idx is not found" {
                    val path = JsPath("user").append("phones").append(1)

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / "user" / "phones" / 1)
                }

                "a node is invalid type" {
                    val path = JsPath("user").append("name").append(0)

                    val result = JsLookup.apply(JsLocation.Root, path, node)

                    result shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.Root / "user" / "name",
                        expected = JsValue.Type.ARRAY,
                        actual = JsValue.Type.STRING
                    )
                }
            }
        }

        "JsLookup.Defined#apply(String)" - {
            val lookup = JsLookup.Defined(JsLocation.Root, JsObject("name" to JsString(USER_NAME_VALUE)))

            "a key is found" {
                val key = "name"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Defined(JsLocation.Root / key, JsString(USER_NAME_VALUE))
            }

            "a key is not found" {
                val key = "user"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / key)
            }

            "a node is invalid type" {
                val idx = 0

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.Root,
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.OBJECT
                )
            }
        }

        "JsLookup.Defined#apply(Int)" - {
            val lookup = JsLookup.Defined(JsLocation.Root, JsArray(JsString(FIRST_PHONE_VALUE)))

            "an idx is found" {
                val idx = 0

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Defined(JsLocation.Root / idx, JsString(FIRST_PHONE_VALUE))
            }

            "an idx is not found" {
                val idx = 1

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Undefined.PathMissing(JsLocation.Root / idx)
            }

            "a node is invalid type" {
                val key = "name"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.Root,
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.ARRAY
                )
            }
        }

        "JsLookup.Undefined#apply(String)" - {

            "return same 'JsLookup.Undefined'" {
                val lookup = JsLookup.Undefined.PathMissing(JsLocation.Root)

                val result = lookup.apply("name")

                assertEquals(lookup, result)
            }
        }

        "JsLookup.Undefined#apply(Int)" - {

            "return same 'JsLookup.Undefined'" {
                val lookup = JsLookup.Undefined.PathMissing(JsLocation.Root)

                val result = lookup.apply(0)

                assertEquals(lookup, result)
            }
        }
    }
}
