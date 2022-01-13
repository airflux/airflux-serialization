package io.github.airflux.lookup

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import kotlin.test.assertEquals

class JsLookupTest : FreeSpec() {

    init {

        "Calling function 'JsLookup.apply'" - {

            "parameter 'name' is exists named path element" - {
                val key = KeyPathElement("name")

                "parameter 'value' is 'JsObject'" - {
                    val value = JsObject("name" to JsString(USER_NAME_VALUE))

                    "should return 'JsLookup.Defined'" {
                        val result = JsLookup.apply(JsLocation.Root, key, value)

                        result as JsLookup.Defined
                        assertEquals(JsLocation.Root / key, result.location)
                        assertEquals(USER_NAME_VALUE, (result.value as JsString).get)
                    }
                }

                "parameter 'value' is not 'JsObject'" - {
                    val value = JsString(USER_NAME_VALUE)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        val result = JsLookup.apply(JsLocation.Root, key, value)

                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.OBJECT, result.expected)
                        assertEquals(JsValue.Type.STRING, result.actual)
                    }
                }
            }

            "parameter 'name' is not exists named path element" - {
                val key = KeyPathElement("user")

                "parameter 'value' is 'JsObject'" - {
                    val value = JsObject("name" to JsString(USER_NAME_VALUE))

                    "should return 'JsLookup.Undefined.PathMissing'" {
                        val result = JsLookup.apply(JsLocation.Root, key, value)

                        result as JsLookup.Undefined.PathMissing
                        assertEquals(JsLocation.Root / key, result.location)
                    }
                }

                "parameter 'value' is not 'JsObject'" - {
                    val value = JsString(USER_NAME_VALUE)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        val result = JsLookup.apply(JsLocation.Root, key, value)

                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.OBJECT, result.expected)
                        assertEquals(JsValue.Type.STRING, result.actual)
                    }
                }
            }

            "parameter 'idx' is exists index path element" - {
                val idx = IdxPathElement(0)

                "parameter 'value' is 'JsArray'" {
                    val value = JsArray(JsString(FIRST_PHONE_VALUE))

                    "should return 'JsLookup.Defined'" - {
                        val result = JsLookup.apply(JsLocation.Root, idx, value)

                        result as JsLookup.Defined
                        assertEquals(JsLocation.Root / idx, result.location)
                        assertEquals(FIRST_PHONE_VALUE, (result.value as JsString).get)
                    }
                }

                "parameter 'value' is not 'JsArray'" - {
                    val value = JsString(USER_NAME_VALUE)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        val result = JsLookup.apply(JsLocation.Root, idx, value)

                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.ARRAY, result.expected)
                        assertEquals(JsValue.Type.STRING, result.actual)
                    }
                }
            }

            "parameter 'idx' is not exists named path element" - {
                val idx = IdxPathElement(1)

                "parameter 'value' is 'JsArray'" - {
                    val value = JsArray(JsString(FIRST_PHONE_VALUE))

                    "should return 'JsLookup.Undefined.PathMissing'" {
                        val result = JsLookup.apply(JsLocation.Root, idx, value)

                        result as JsLookup.Undefined.PathMissing
                        assertEquals(JsLocation.Root / idx, result.location)
                    }
                }

                "parameter 'value' is not 'JsArray'" - {
                    val value = JsString(USER_NAME_VALUE)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        val result = JsLookup.apply(JsLocation.Root, idx, value)

                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.ARRAY, result.expected)
                        assertEquals(JsValue.Type.STRING, result.actual)
                    }
                }
            }
        }

        "JsLookup.Defined" - {

            "property value has 'JsObject' value" - {
                val value = JsObject("name" to JsString(USER_NAME_VALUE))
                val lookup = JsLookup.Defined(JsLocation.Root, value)

                "calling function 'apply' with parameter 'name' is exists named path element" - {
                    val key = "name"
                    val result = lookup.apply(key)

                    "should return 'JsLookup.Defined'" {
                        result as JsLookup.Defined
                        assertEquals(JsLocation.Root / key, result.location)
                        assertEquals(USER_NAME_VALUE, (result.value as JsString).get)
                    }
                }

                "calling function 'apply' with parameter 'name' is not exists named path element" - {
                    val key = "user"
                    val result = lookup.apply(key)

                    "should return 'JsLookup.Undefined.PathMissing'" {
                        result as JsLookup.Undefined.PathMissing
                        assertEquals(JsLocation.Root / key, result.location)
                    }
                }

                "calling function 'apply' with parameter 'idx' is some index path element" - {
                    val idx = 0
                    val result = lookup.apply(idx)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.ARRAY, result.expected)
                        assertEquals(JsValue.Type.OBJECT, result.actual)
                    }
                }
            }

            "property value has 'JsArray' value" - {
                val value = JsArray(JsString(FIRST_PHONE_VALUE))
                val lookup = JsLookup.Defined(JsLocation.Root, value)

                "calling function 'apply' with parameter 'idx' is exists index path element" - {
                    val idx = 0
                    val result = lookup.apply(idx)

                    "should return 'JsLookup.Defined'" {
                        result as JsLookup.Defined
                        assertEquals(JsLocation.Root / idx, result.location)
                        assertEquals(FIRST_PHONE_VALUE, (result.value as JsString).get)
                    }
                }

                "calling function 'apply' with parameter 'idx' is not exists index path element" - {
                    val idx = 1
                    val result = lookup.apply(idx)

                    "should return 'JsLookup.Undefined.PathMissing'" {
                        result as JsLookup.Undefined.PathMissing
                        assertEquals(JsLocation.Root / idx, result.location)
                    }
                }

                "calling function 'apply' with parameter 'name' is some named path element" - {
                    val key = "name"
                    val result = lookup.apply(key)

                    "should return 'JsLookup.Undefined.InvalidType'" {
                        result as JsLookup.Undefined.InvalidType
                        assertEquals(JsLocation.Root, result.location)
                        assertEquals(JsValue.Type.OBJECT, result.expected)
                        assertEquals(JsValue.Type.ARRAY, result.actual)
                    }
                }
            }
        }

        "JsLookup.Undefined" - {
            val lookup = JsLookup.Undefined.PathMissing(JsLocation.Root)

            "calling function 'apply' with parameter 'name' is some named path element" - {
                val result = lookup.apply("name")

                "should return same 'JsLookup.Undefined'" {
                    assertEquals(lookup, result)
                }
            }

            "calling function 'apply' with parameter 'idx' is some index path element" - {
                val result = lookup.apply(0)

                "should return same 'JsLookup.Undefined'" {
                    assertEquals(lookup, result)
                }
            }
        }
    }
}
