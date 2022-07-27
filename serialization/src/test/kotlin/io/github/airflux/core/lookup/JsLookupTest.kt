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

package io.github.airflux.core.lookup

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.PathElement
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertEquals

internal class JsLookupTest : FreeSpec() {

    init {

        "JsLookup#apply(_, PathElement.Key, _)" - {

            "a key is found" {
                val node = JsObject("name" to JsString(USER_NAME_VALUE))
                val key = "name"

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Defined(JsLocation.empty.append(key), JsString(USER_NAME_VALUE))
            }

            "a key is not found" {
                val node = JsObject("name" to JsString(USER_NAME_VALUE))
                val key = "user"

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(key))
            }

            "a node is invalid type" {
                val node = JsString(USER_NAME_VALUE)
                val key = "user"

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Key(key), node)

                lookup shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty,
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.STRING
                )
            }
        }

        "JsLookup#apply(_, PathElement.Idx, _)" - {

            "an idx is found" {
                val node = JsArray(JsString(FIRST_PHONE_VALUE))
                val idx = 0

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Defined(JsLocation.empty.append(idx), JsString(FIRST_PHONE_VALUE))
            }

            "an idx is not found" {
                val node = JsArray(JsString(FIRST_PHONE_VALUE))
                val idx = 1

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(idx))
            }

            "a node is invalid type" {
                val node = JsString(USER_NAME_VALUE)
                val idx = 0

                val lookup = JsLookup.apply(JsLocation.empty, PathElement.Idx(idx), node)

                lookup shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty,
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

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Defined(
                        JsLocation.empty.append("user").append("name"),
                        JsString(USER_NAME_VALUE)
                    )
                }

                "a key is not found" {
                    val path = JsPath("user").append("id")

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append("user").append("id"))
                }

                "a node is invalid type" {
                    val path = JsPath("user").append("phones").append("mobile")

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty.append("user").append("phones"),
                        expected = JsValue.Type.OBJECT,
                        actual = JsValue.Type.ARRAY
                    )
                }
            }

            "indexed element" - {

                "an idx is found" {
                    val path = JsPath("user").append("phones").append(0)

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Defined(
                        JsLocation.empty.append("user").append("phones").append(0),
                        JsString(FIRST_PHONE_VALUE)
                    )
                }

                "an idx is not found" {
                    val path = JsPath("user").append("phones").append(1)

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Undefined.PathMissing(
                        JsLocation.empty.append("user").append("phones").append(1)
                    )
                }

                "a node is invalid type" {
                    val path = JsPath("user").append("name").append(0)

                    val result = JsLookup.apply(JsLocation.empty, path, node)

                    result shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty.append("user").append("name"),
                        expected = JsValue.Type.ARRAY,
                        actual = JsValue.Type.STRING
                    )
                }
            }
        }

        "JsLookup.Defined#apply(String)" - {
            val lookup = JsLookup.Defined(JsLocation.empty, JsObject("name" to JsString(USER_NAME_VALUE)))

            "a key is found" {
                val key = "name"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Defined(JsLocation.empty.append(key), JsString(USER_NAME_VALUE))
            }

            "a key is not found" {
                val key = "user"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(key))
            }

            "a node is invalid type" {
                val idx = 0

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty,
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.OBJECT
                )
            }
        }

        "JsLookup.Defined#apply(Int)" - {
            val lookup = JsLookup.Defined(JsLocation.empty, JsArray(JsString(FIRST_PHONE_VALUE)))

            "an idx is found" {
                val idx = 0

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Defined(JsLocation.empty.append(idx), JsString(FIRST_PHONE_VALUE))
            }

            "an idx is not found" {
                val idx = 1

                val result = lookup.apply(idx)

                result shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(idx))
            }

            "a node is invalid type" {
                val key = "name"

                val result = lookup.apply(key)

                result shouldBe JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty,
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.ARRAY
                )
            }
        }

        "JsLookup.Undefined#apply(String)" - {

            "return same 'JsLookup.Undefined'" {
                val lookup = JsLookup.Undefined.PathMissing(JsLocation.empty)

                val result = lookup.apply("name")

                assertEquals(lookup, result)
            }
        }

        "JsLookup.Undefined#apply(Int)" - {

            "return same 'JsLookup.Undefined'" {
                val lookup = JsLookup.Undefined.PathMissing(JsLocation.empty)

                val result = lookup.apply(0)

                assertEquals(lookup, result)
            }
        }
    }
}
