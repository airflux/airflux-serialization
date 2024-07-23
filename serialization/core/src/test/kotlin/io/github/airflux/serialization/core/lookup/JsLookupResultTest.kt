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

package io.github.airflux.serialization.core.lookup

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsLookupResultTest : FreeSpec() {

    companion object {
        private const val ACCOUNT = "account"
        private const val USER = "user"
        private const val PHONES = "phones"
        private const val ID = "id"
        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"
        private const val IDX = 0
        private const val UNKNOWN_IDX = 1

        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The `JsLookupResult` type" - {

            "The lookup by a key element" - {
                withData(
                    listOf(
                        //Defined
                        TestCaseData.WithKey(
                            description = "1",
                            source = JsStruct(ID to JsString(VALUE)),
                            key = ID,
                            result = JsLookupResult.Defined(location = LOCATION.append(ID), value = JsString(VALUE))
                        ),

                        //Source is empty struct
                        TestCaseData.WithKey(
                            description = "2.1",
                            source = JsStruct(),
                            key = ID,
                            result = JsLookupResult.Undefined.PathMissing(LOCATION.append(ID))
                        ),

                        //Find unknown key
                        TestCaseData.WithKey(
                            description = "3.1",
                            source = JsStruct(
                                USER to JsString(VALUE)
                            ),
                            key = ID,
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(ID))
                        ),
                        TestCaseData.WithKey(
                            description = "3.2",
                            source = JsStruct(
                                USER to JsStruct(
                                    ID to JsString(VALUE)
                                )
                            ),
                            key = ID,
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(ID))
                        ),

                        //Invalid source node (expected a structure)
                        TestCaseData.WithKey(
                            description = "4",
                            source = JsString(VALUE),
                            key = ID,
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        )
                    )
                ) { data ->
                    val lookup = data.source.lookup(location = LOCATION, key = data.key)
                    lookup shouldBe data.result
                }
            }

            "The lookup by an index element" - {
                withData(
                    listOf(
                        //Defined
                        TestCaseData.WithIndex(
                            description = "1",
                            source = JsArray(JsString(VALUE)),
                            index = IDX,
                            result = JsLookupResult.Defined(location = LOCATION.append(IDX), value = JsString(VALUE))
                        ),

                        //Source is empty array
                        TestCaseData.WithIndex(
                            description = "2",
                            source = JsArray(),
                            index = IDX,
                            result = JsLookupResult.Undefined.PathMissing(LOCATION.append(IDX))
                        ),

                        //Find unknown index
                        TestCaseData.WithIndex(
                            description = "3",
                            source = JsArray(JsString(VALUE)),
                            index = UNKNOWN_IDX,
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(UNKNOWN_IDX))
                        ),

                        //Invalid source node (expected an array)
                        TestCaseData.WithIndex(
                            description = "4",
                            source = JsString(VALUE),
                            index = IDX,
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING
                            )
                        )
                    )
                ) { data ->
                    val lookup = data.source.lookup(location = LOCATION, idx = data.index)
                    lookup shouldBe data.result
                }
            }

            "The lookup by a path" - {
                withData(
                    listOf(
                        //Defined level - 0
                        TestCaseData.WithPath(
                            description = "1.1",
                            source = JsStruct(ID to JsString(VALUE)),
                            path = JsPath(ID),
                            result = JsLookupResult.Defined(location = LOCATION.append(ID), value = JsString(VALUE))
                        ),
                        TestCaseData.WithPath(
                            description = "1.2",
                            source = JsArray(JsString(VALUE)),
                            path = JsPath(IDX),
                            result = JsLookupResult.Defined(location = LOCATION.append(IDX), value = JsString(VALUE))
                        ),

                        //Defined level - 1
                        TestCaseData.WithPath(
                            description = "2.1",
                            source = JsStruct(
                                USER to JsStruct(
                                    ID to JsString(VALUE)
                                )
                            ),
                            path = JsPath(USER).append(ID),
                            result = JsLookupResult.Defined(
                                location = LOCATION.append(USER).append(ID),
                                value = JsString(VALUE)
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "2.2",
                            source = JsStruct(PHONES to JsArray(JsString(VALUE))),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Defined(
                                location = LOCATION.append(PHONES).append(IDX),
                                value = JsString(VALUE)
                            )
                        ),

                        //Source is empty struct
                        TestCaseData.WithPath(
                            description = "3.1",
                            source = JsStruct(),
                            path = JsPath(ID),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(ID)
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "3.2",
                            source = JsStruct(),
                            path = JsPath(USER).append(ID),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(USER).append(ID)
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "3.3",
                            source = JsStruct(),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(PHONES).append(IDX)
                            )
                        ),

                        //Source is empty array
                        TestCaseData.WithPath(
                            description = "4.1",
                            source = JsArray(),
                            path = JsPath(IDX),
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(IDX))
                        ),
                        TestCaseData.WithPath(
                            description = "4.2",
                            source = JsArray(),
                            path = JsPath(IDX).append(ID),
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(IDX).append(ID))
                        ),
                        TestCaseData.WithPath(
                            description = "4.3",
                            source = JsArray(),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.ARRAY
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "4.4",
                            source = JsStruct(PHONES to JsArray()),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(PHONES).append(IDX)
                            )
                        ),

                        //Invalid source node (expected a structure), level - 0
                        TestCaseData.WithPath(
                            description = "5.1",
                            source = JsString(VALUE),
                            path = JsPath(ID),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "5.2",
                            source = JsString(VALUE),
                            path = JsPath(USER).append(ID),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "5.3",
                            source = JsString(VALUE),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),

                        //Invalid source node (expected a structure), level - 1
                        TestCaseData.WithPath(
                            description = "6.1",
                            source = JsStruct(ACCOUNT to JsString(VALUE)),
                            path = JsPath(ACCOUNT).append(USER),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION.append(ACCOUNT),
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "6.2",
                            source = JsStruct(ACCOUNT to JsString(VALUE)),
                            path = JsPath(ACCOUNT).append(USER).append(ID),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION.append(ACCOUNT),
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "6.3",
                            source = JsStruct(ACCOUNT to JsString(VALUE)),
                            path = JsPath(ACCOUNT).append(USER).append(IDX),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION.append(ACCOUNT),
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING
                            )
                        ),

                        //Invalid source node (expected an array), level - 0
                        TestCaseData.WithPath(
                            description = "7.1",
                            source = JsString(VALUE),
                            path = JsPath(IDX),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "7.2",
                            source = JsString(VALUE),
                            path = JsPath(IDX).append(ID),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION,
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING
                            )
                        ),

                        //Invalid source node (expected an array), level - 1
                        TestCaseData.WithPath(
                            description = "8.1",
                            source = JsStruct(PHONES to JsString(VALUE)),
                            path = JsPath(PHONES).append(IDX),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION.append(PHONES),
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING
                            )
                        ),
                        TestCaseData.WithPath(
                            description = "8.2",
                            source = JsStruct(PHONES to JsString(VALUE)),
                            path = JsPath(PHONES).append(IDX).append(ID),
                            result = JsLookupResult.Undefined.InvalidType(
                                breakpoint = LOCATION.append(PHONES),
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING
                            )
                        ),

                        //Find unknown key
                        TestCaseData.WithPath(
                            description = "9.1",
                            source = JsStruct(ID to JsString(VALUE)),
                            path = JsPath(USER),
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(USER))
                        ),
                        TestCaseData.WithPath(
                            description = "9.2",
                            source = JsStruct(
                                USER to JsStruct(
                                    ID to JsString(VALUE)
                                )
                            ),
                            path = JsPath(ACCOUNT).append(USER),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(ACCOUNT).append(USER)
                            )
                        ),

                        //Find unknown index
                        TestCaseData.WithPath(
                            description = "10.1",
                            source = JsArray(JsString(VALUE)),
                            path = JsPath(UNKNOWN_IDX),
                            result = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(UNKNOWN_IDX))
                        ),
                        TestCaseData.WithPath(
                            description = "10.2",
                            source = JsStruct(PHONES to JsArray(JsString(VALUE))),
                            path = JsPath(PHONES).append(UNKNOWN_IDX),
                            result = JsLookupResult.Undefined.PathMissing(
                                location = LOCATION.append(PHONES).append(UNKNOWN_IDX)
                            )
                        )
                    )
                ) { data ->
                    val lookup = data.source.lookup(location = LOCATION, path = data.path)
                    lookup shouldBe data.result
                }
            }

            "The `JsLookupResult#Defined` type" - {

                "when lookup by a key element" - {
                    val searchKey = ID

                    "when the source is the JsStruct type" - {

                        "when the value is empty" - {
                            val defined = JsLookupResult.Defined(location = LOCATION.append(USER), value = JsStruct())

                            "then should return the value as an instance of the Undefined#PathMissing type" {
                                val lookup = defined.apply(searchKey)
                                lookup shouldBe JsLookupResult.Undefined.PathMissing(
                                    location = LOCATION.append(USER).append(searchKey)
                                )
                            }
                        }

                        "when the source is not empty" - {

                            "when the source contains the finding key" - {
                                val defined = JsLookupResult.Defined(
                                    location = LOCATION.append(USER),
                                    value = JsStruct(ID to JsString(VALUE))
                                )

                                "then should return the value as an instance of the Defined type" {
                                    val lookup = defined.apply(searchKey)
                                    lookup shouldBe JsLookupResult.Defined(
                                        location = LOCATION.append(USER).append(searchKey),
                                        value = JsString(VALUE)
                                    )
                                }
                            }

                            "when the source does not contain the finding key" - {
                                val defined = JsLookupResult.Defined(
                                    location = LOCATION.append(USER),
                                    value = JsStruct(PHONES to JsString(VALUE))
                                )

                                "then should return the value as an instance of the Undefined#PathMissing type" {
                                    val lookup = defined.apply(searchKey)
                                    lookup shouldBe JsLookupResult.Undefined.PathMissing(
                                        location = LOCATION.append(USER).append(searchKey)
                                    )
                                }
                            }
                        }
                    }

                    "when the source is not the JsStruct type" - {

                        val defined = JsLookupResult.Defined(location = LOCATION.append(USER), value = JsString(VALUE))

                        "then should return the value as an instance of the Undefined#InvalidType type" {
                            val lookup = defined.apply(searchKey)
                            lookup shouldBe JsLookupResult.Undefined.InvalidType(
                                expected = listOf(JsValue.Type.STRUCT),
                                actual = JsValue.Type.STRING,
                                breakpoint = LOCATION.append(USER)
                            )
                        }
                    }
                }

                "when lookup by an index element" - {

                    "when the source is the JsArray type" - {

                        "when the source is empty" - {
                            val searchIndex = IDX
                            val defined =
                                JsLookupResult.Defined(location = LOCATION.append(PHONES), value = JsArray())

                            "then should return the value as an instance of the Undefined#PathMissing type" {
                                val lookup = defined.apply(searchIndex)
                                lookup shouldBe JsLookupResult.Undefined.PathMissing(
                                    location = LOCATION.append(PHONES).append(searchIndex)
                                )
                            }
                        }

                        "when the source is not empty" - {
                            val defined = JsLookupResult.Defined(
                                location = LOCATION.append(PHONES),
                                value = JsArray(JsString(VALUE))
                            )

                            "when the source contains the finding index" - {
                                val searchIndex = IDX

                                "then should return the value as an instance of the Defined type" {
                                    val lookup = defined.apply(searchIndex)
                                    lookup shouldBe JsLookupResult.Defined(
                                        location = LOCATION.append(PHONES).append(searchIndex),
                                        value = JsString(VALUE)
                                    )
                                }
                            }

                            "when the source does not contain the finding index" - {
                                val searchIndex = UNKNOWN_IDX

                                "then should return the value as an instance of the Undefine#PathMissingd type" {
                                    val lookup = defined.apply(searchIndex)
                                    lookup shouldBe JsLookupResult.Undefined.PathMissing(
                                        location = LOCATION.append(PHONES).append(searchIndex)
                                    )
                                }
                            }
                        }
                    }

                    "when the source is not the JsArray type" - {
                        val searchIndex = IDX
                        val defined = JsLookupResult.Defined(location = LOCATION.append(USER), value = JsString(VALUE))

                        "then should return the value as an instance of the Undefined#InvalidType type" {
                            val lookup = defined.apply(searchIndex)
                            lookup shouldBe JsLookupResult.Undefined.InvalidType(
                                expected = listOf(JsValue.Type.ARRAY),
                                actual = JsValue.Type.STRING,
                                breakpoint = LOCATION.append(USER)
                            )
                        }
                    }
                }
            }

            "The `JsLookupResult#Undefined#InvalidType` type" - {
                val undefined = JsLookupResult.Undefined.InvalidType(
                    expected = listOf(JsValue.Type.ARRAY),
                    actual = JsValue.Type.STRING,
                    breakpoint = LOCATION.append(USER)
                )

                "when lookup by a key element" - {
                    val searchKey = ID

                    "then should return the same instance of the Undefined type" {
                        val lookup = undefined.apply(searchKey)
                        lookup shouldBeSameInstanceAs undefined
                    }
                }

                "when lookup by an index element" - {
                    val searchIndex = IDX

                    "then should return the same instance of the Undefined type" {
                        val lookup = undefined.apply(searchIndex)
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }

            "The `JsLookupResult#Undefined#PathMissing` type" - {
                val undefined = JsLookupResult.Undefined.PathMissing(location = LOCATION.append(USER))

                "when lookup by a key element" - {
                    val searchKey = ID

                    "then should return the new instance of the Undefined#PathMissing type" {
                        val lookup = undefined.apply(searchKey)
                        lookup shouldBe JsLookupResult.Undefined.PathMissing(
                            location = LOCATION.append(USER).append(ID)
                        )
                    }
                }

                "when lookup by an index element" - {
                    val searchIndex = IDX

                    "then should return the new instance of the Undefined#PathMissing type" {
                        val lookup = undefined.apply(searchIndex)
                        lookup shouldBe JsLookupResult.Undefined.PathMissing(
                            location = LOCATION.append(USER).append(IDX)
                        )
                    }
                }
            }
        }
    }

    internal sealed class TestCaseData {

        internal class WithKey(
            val description: String,
            val source: JsValue,
            key: String,
            val result: JsLookupResult
        ) : WithDataTestName {
            val key: JsPath.Element.Key = JsPath.Element.Key(key)
            override fun dataTestName(): String = "$description. source: $source, key: $key, result: $result"
        }

        internal class WithIndex(
            val description: String,
            val source: JsValue,
            index: Int,
            val result: JsLookupResult
        ) : WithDataTestName {
            val index: JsPath.Element.Idx = JsPath.Element.Idx(index)
            override fun dataTestName(): String = "$description. source: $source, index: $index, result: $result"
        }

        internal class WithPath(
            val description: String,
            val source: JsValue,
            val path: JsPath,
            val result: JsLookupResult
        ) : TestCaseData(), WithDataTestName {
            override fun dataTestName(): String = "$description. source: $source, path: $path, result: $result"
        }
    }
}
