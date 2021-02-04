package io.github.airflux.reader

import io.github.airflux.common.TestData.DEFAULT_PHONE_VALUE
import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.dsl.CollectionBuilderFactory
import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PathReaderTest {

    companion object {
        val stringReader = JsReader { input -> input.readAsString() }
    }

    @Nested
    inner class Required {

        @Test
        fun `Testing 'required' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 0
            val pathReader: JsReader<String> = JsReader.required(path = path, reader = stringReader)

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(FIRST_PHONE_VALUE, result.value)
        }

        @Test
        fun `Testing 'required' function (an attribute is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    ),
                )
            )
            val path = "user" / "phones" / 1
            val pathReader: JsReader<String> = JsReader.required(path = path, reader = stringReader)

            val result = pathReader.read(json)

            result as JsResult.Failure
            val reasons = result.errors
            assertEquals(1, reasons.size)

            reasons[0].also { (pathError, errors) ->
                assertEquals(path, pathError)
                assertEquals(1, errors.size)
                assertTrue(errors[0] is JsError.PathMissing)
            }
        }
    }

    @Nested
    inner class OrDefault {

        @Test
        fun `Testing 'orDefault' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 0
            val pathReader: JsReader<String> =
                JsReader.orDefault(path = path, reader = stringReader, defaultValue = { DEFAULT_PHONE_VALUE })

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(FIRST_PHONE_VALUE, result.value)
        }

        @Test
        fun `Testing 'orDefault' function (an attribute is found, returning default value)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 1
            val pathReader: JsReader<String> =
                JsReader.orDefault(path = path, reader = stringReader, defaultValue = { "default" })

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals("default", result.value)
        }
    }

    @Nested
    inner class Nullable {

        @Test
        fun `Testing 'nullable' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 0
            val pathReader: JsReader<String?> = JsReader.nullable(path = path, reader = stringReader)

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(FIRST_PHONE_VALUE, result.value)
        }

        @Test
        fun `Testing 'nullable' function (an attribute is found with value 'null')`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "role" to JsNull
                )
            )
            val path = "user" / "role"
            val pathReader: JsReader<String?> = JsReader.nullable(path = path, reader = stringReader)

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertNull(result.value)
        }

        @Test
        fun `Testing 'nullable' function (an attribute is not found, returning value 'null')`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 1
            val pathReader: JsReader<String?> = JsReader.nullable(path = path, reader = stringReader)

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertNull(result.value)
        }
    }

    @Nested
    inner class OptionalOrDefault {

        @Test
        fun `Testing 'nullableOrDefault' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones" / 0
            val pathReader: JsReader<String?> =
                JsReader.nullableOrDefault(path = path, reader = stringReader, defaultValue = { DEFAULT_PHONE_VALUE })

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(FIRST_PHONE_VALUE, result.value)
        }

        @Test
        fun `Testing 'nullableOrDefault' function (an attribute is found with value 'null')`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "role" to JsNull
                )
            )
            val path = "user" / "role"
            val pathReader: JsReader<String?> =
                JsReader.nullableOrDefault(path = path, reader = stringReader, defaultValue = { DEFAULT_PHONE_VALUE })

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertNull(result.value)
        }

        @Test
        fun `Testing 'nullableOrDefault' function (an attribute is not found, returning default value)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "role"
            val pathReader: JsReader<String?> =
                JsReader.nullableOrDefault(path = path, reader = stringReader, defaultValue = { "default" })

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals("default", result.value)
        }
    }

    @Nested
    inner class Traversable {

        @Test
        fun `Testing 'traversable' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                    )
                )
            )
            val path = "user" / "phones"
            val pathReader: JsReader<List<String>> = JsReader.traversable(
                path = path,
                reader = stringReader,
                factory = CollectionBuilderFactory.listFactory()
            )

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
        }

        @Test
        fun `Testing 'traversable' function (empty array found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray<JsString>()
                )
            )
            val path = "user" / "phones"
            val pathReader: JsReader<List<String>> = JsReader.traversable(
                path = path,
                reader = stringReader,
                factory = CollectionBuilderFactory.listFactory()
            )

            val result = pathReader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(listOf(), result.value)
        }

        @Test
        fun `Testing 'traversable' function (an attribute is not collection)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "role" to JsString("employee"),
                )
            )
            val path = "user" / "role"
            val pathReader: JsReader<List<String>> = JsReader.traversable(
                path = path,
                reader = stringReader,
                factory = CollectionBuilderFactory.listFactory()
            )

            val result = pathReader.read(json)

            result as JsResult.Failure
            val reasons = result.errors
            assertEquals(1, reasons.size)

            reasons[0].also { (pathError, errors) ->
                assertEquals(path, pathError)
                assertEquals(1, errors.size)
                assertTrue(errors[0] is JsError.InvalidType)
            }
        }

        @Test
        fun `Testing 'traversable' function (collection with inconsistent content)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE),
                        JsNumber.valueOf(10),
                        JsBoolean.True,
                        JsString(SECOND_PHONE_VALUE),
                    ),
                )
            )
            val path = "user" / "phones"
            val pathReader: JsReader<List<String>> = JsReader.traversable(
                path = path,
                reader = stringReader,
                factory = CollectionBuilderFactory.listFactory()
            )

            val result = pathReader.read(json)

            result as JsResult.Failure
            val reasons = result.errors
            assertEquals(2, reasons.size)

            reasons[0].also { (pathError, errors) ->
                assertEquals(path / 1, pathError)
                assertEquals(1, errors.size)
            }

            reasons[1].also { (pathError, errors) ->
                assertEquals(path / 2, pathError)
                assertEquals(1, errors.size)
            }
        }
    }
}
