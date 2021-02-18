package io.github.airflux.reader

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.path.JsPath
import io.github.airflux.reader.TraversableReader.Companion.list
import io.github.airflux.reader.TraversableReader.Companion.set
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class TraversablePathReaderTest {

    companion object {
        val stringReader: JsReader<String> = JsReader { input ->
            when (input) {
                is JsString -> JsResult.Success(input.underlying)
                else -> JsResult.Failure(
                    error = JsError.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    @Nested
    inner class ListReader {

        @Test
        fun `Testing 'list' function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
            val reader: JsReader<List<String>> = list(using = stringReader)

            val result: JsResult<List<String>> = reader.read(json)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
        }

        @Test
        fun `Testing 'list' function (an attribute is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)
            val reader: JsReader<List<String>> = list(using = stringReader)

            val result: JsResult<List<String>> = reader.read(json)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsError.InvalidType
                    assertEquals(JsValue.Type.ARRAY, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
        }

        @Test
        fun `Testing 'list' function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )
            val reader: JsReader<List<String>> = list(using = stringReader)

            val result: JsResult<List<String>> = reader.read(json)

            result as JsResult.Failure
            assertEquals(2, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 1, pathError)
                    assertEquals(1, errors.size)
                }

            result.errors[1]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 2, pathError)
                    assertEquals(1, errors.size)
                }
        }

        @Test
        fun `Testing 'list' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val reader: JsReader<List<String>> = list(using = stringReader)

            val result: JsResult<List<String>> = reader.read(json)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(listOf(), result.value)
        }
    }

    @Nested
    inner class SetReader {

        @Test
        fun `Testing 'set' function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
            val reader: JsReader<Set<String>> = set(using = stringReader)

            val result: JsResult<Set<String>> = reader.read(json)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
        }

        @Test
        fun `Testing 'set' function (an attribute is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)
            val reader: JsReader<Set<String>> = set(using = stringReader)

            val result: JsResult<Set<String>> = reader.read(json)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsError.InvalidType
                    assertEquals(JsValue.Type.ARRAY, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
        }

        @Test
        fun `Testing 'set' function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )
            val reader: JsReader<Set<String>> = set(using = stringReader)

            val result: JsResult<Set<String>> = reader.read(json)

            result as JsResult.Failure
            assertEquals(2, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 1, pathError)
                    assertEquals(1, errors.size)
                }

            result.errors[1]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 2, pathError)
                    assertEquals(1, errors.size)
                }
        }

        @Test
        fun `Testing 'set' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()
            val reader: JsReader<Set<String>> = set(using = stringReader)

            val result: JsResult<Set<String>> = reader.read(json)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(setOf(), result.value)
        }
    }
}

/*class TraversablePathReaderTest {

    companion object {
        val stringReader: JsReader<String> = JsReader { input ->
            when (input) {
                is JsString -> JsResult.Success(input.underlying)
                else -> JsResult.Failure(
                    error = JsError.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    @Nested
    inner class FromJsLookup {

        @Nested
        inner class ListCollection {

            @Test
            fun `Testing 'list' function (an attribute is found)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
            }

            @Test
            fun `Testing 'list' function (an attribute is not found)`() {
                val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsPath.empty / "phones")

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        assertTrue(errors[0] is JsError.PathMissing)
                    }
            }

            @Test
            fun `Testing 'list' function (an attribute is invalid type)`() {
                val from: JsLookup = JsLookup.Undefined.InvalidType(
                    path = JsPath.empty / "phones",
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.STRING
                )

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'list' function (an attribute is not collection)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsString(USER_NAME_VALUE)
                )

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'list' function (collection with inconsistent content)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray(
                        JsString(FIRST_PHONE_VALUE),
                        JsNumber.valueOf(10),
                        JsBoolean.True,
                        JsString(SECOND_PHONE_VALUE),
                    )
                )

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(2, result.errors.size)

                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 1, pathError)
                        assertEquals(1, errors.size)
                    }

                result.errors[1]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 2, pathError)
                        assertEquals(1, errors.size)
                    }
            }

            @Test
            fun `Testing 'list' function (array is empty)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray<JsString>()
                )

                val result: JsResult<List<String>> = list(from = from, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(listOf(), result.value)
            }
        }

        @Nested
        inner class SetCollection {

            @Test
            fun `Testing 'set' function (an attribute is found)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
            }

            @Test
            fun `Testing 'set' function (an attribute is not found)`() {
                val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsPath.empty / "phones")

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        assertTrue(errors[0] is JsError.PathMissing)
                    }
            }

            @Test
            fun `Testing 'set' function (an attribute is invalid type)`() {
                val from: JsLookup = JsLookup.Undefined.InvalidType(
                    path = JsPath.empty / "phones",
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.STRING
                )

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'set' function (an attribute is not collection)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsString(USER_NAME_VALUE)
                )

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'set' function (collection with inconsistent content)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray(
                        JsString(FIRST_PHONE_VALUE),
                        JsNumber.valueOf(10),
                        JsBoolean.True,
                        JsString(SECOND_PHONE_VALUE),
                    )
                )

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Failure
                assertEquals(2, result.errors.size)

                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 1, pathError)
                        assertEquals(1, errors.size)
                    }

                result.errors[1]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 2, pathError)
                        assertEquals(1, errors.size)
                    }
            }

            @Test
            fun `Testing 'set' function (array is empty)`() {
                val from: JsLookup = JsLookup.Defined(
                    path = JsPath.empty / "phones",
                    value = JsArray<JsString>()
                )

                val result: JsResult<Set<String>> = set(from = from, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(setOf(), result.value)
            }
        }
    }

    @Nested
    inner class FromJsValue {

        @Nested
        inner class ListJsValue {
            @Test
            fun `Testing 'list' function (an attribute is found)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
            }

            @Test
            fun `Testing 'list' function (an attribute is not found)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )
                val path = JsPath.empty / "roles"

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "roles", pathError)

                        assertEquals(1, errors.size)
                        assertTrue(errors[0] is JsError.PathMissing)
                    }
            }

            @Test
            fun `Testing 'list' function (an attribute is invalid type)`() {
                val json: JsValue = JsString(USER_NAME_VALUE)
                val path = JsPath.empty / 0

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty, pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'list' function (an attribute is not collection)`() {
                val json: JsValue = JsObject(
                    "phones" to JsString(USER_NAME_VALUE)
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'list' function (collection with inconsistent content)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE),
                        JsNumber.valueOf(10),
                        JsBoolean.True,
                        JsString(SECOND_PHONE_VALUE),
                    )
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(2, result.errors.size)

                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 1, pathError)
                        assertEquals(1, errors.size)
                    }

                result.errors[1]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 2, pathError)
                        assertEquals(1, errors.size)
                    }
            }

            @Test
            fun `Testing 'list' function (array is empty)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray<JsString>()
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<List<String>> = list(from = json, path = path, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(listOf(), result.value)
            }
        }

        @Nested
        inner class SetJsValue {
            @Test
            fun `Testing 'set' function (an attribute is found)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
            }

            @Test
            fun `Testing 'set' function (an attribute is not found)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))
                )
                val path = JsPath.empty / "roles"

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "roles", pathError)

                        assertEquals(1, errors.size)
                        assertTrue(errors[0] is JsError.PathMissing)
                    }
            }

            @Test
            fun `Testing 'set' function (an attribute is invalid type)`() {
                val json: JsValue = JsString(USER_NAME_VALUE)
                val path = JsPath.empty / 0

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty, pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'set' function (an attribute is not collection)`() {
                val json: JsValue = JsObject(
                    "phones" to JsString(USER_NAME_VALUE)
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(1, result.errors.size)
                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones", pathError)

                        assertEquals(1, errors.size)
                        val error = errors[0] as JsError.InvalidType
                        assertEquals(JsValue.Type.ARRAY, error.expected)
                        assertEquals(JsValue.Type.STRING, error.actual)
                    }
            }

            @Test
            fun `Testing 'set' function (collection with inconsistent content)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE),
                        JsNumber.valueOf(10),
                        JsBoolean.True,
                        JsString(SECOND_PHONE_VALUE),
                    )
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Failure
                assertEquals(2, result.errors.size)

                result.errors[0]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 1, pathError)
                        assertEquals(1, errors.size)
                    }

                result.errors[1]
                    .also { (pathError, errors) ->
                        assertEquals(JsPath.empty / "phones" / 2, pathError)
                        assertEquals(1, errors.size)
                    }
            }

            @Test
            fun `Testing 'set' function (array is empty)`() {
                val json: JsValue = JsObject(
                    "phones" to JsArray<JsString>()
                )
                val path = JsPath.empty / "phones"

                val result: JsResult<Set<String>> = set(from = json, path = path, using = stringReader)

                result as JsResult.Success
                assertEquals(JsPath.empty / "phones", result.path)
                assertEquals(setOf(), result.value)
            }
        }
    }
}*/
