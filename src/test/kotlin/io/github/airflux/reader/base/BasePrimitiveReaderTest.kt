package io.github.airflux.reader.base

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BasePrimitiveReaderTest {

    @Nested
    inner class BooleanReader {
        val reader = BasePrimitiveReader.boolean

        @Test
        fun `Testing reader for 'Boolean' type`() {
            val value = true
            val input: JsValue = JsBoolean.valueOf(value)

            val result = reader.read(input)

            result as JsResult.Success
            assertEquals(value, result.value)
            assertEquals(JsPath.empty, result.path)
        }

        @Test
        fun `Testing reader for 'Boolean' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.BOOLEAN, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }
    }

    @Nested
    inner class StringReader {
        val reader = BasePrimitiveReader.string

        @Test
        fun `Testing reader for 'String' type`() {
            val value = "abc"
            val input: JsValue = JsString(value)

            val result = reader.read(input)

            result as JsResult.Success
            assertEquals(value, result.value)
            assertEquals(JsPath.empty, result.path)
        }

        @Test
        fun `Testing reader for 'String' type (reading from invalid node)`() {
            val input: JsValue = JsBoolean.valueOf(true)

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.STRING, error.expected)
            assertEquals(JsValue.Type.BOOLEAN, error.actual)
        }
    }

    @Nested
    inner class ByteReader {
        val reader = BasePrimitiveReader.byte

        @TestFactory
        fun `Testing reader for 'Byte' type`(): Collection<DynamicTest> =
            listOf(
                "min value" to Byte.MIN_VALUE,
                "max value" to Byte.MAX_VALUE
            ).map { (displayName: String, value: Byte) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(input)

                    result as JsResult.Success
                    assertEquals(value, result.value)
                    assertEquals(JsPath.empty, result.path)
                }
            }

        @Test
        fun `Testing reader for 'Byte' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }

        @Test
        fun `Testing reader for 'Byte' type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals(Long.MAX_VALUE.toString(), error.value)
            assertEquals(Byte::class, error.type)
        }

        @Test
        fun `Testing reader for 'Byte' type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals("10.5", error.value)
            assertEquals(Byte::class, error.type)
        }
    }

    @Nested
    inner class ShortReader {
        val reader = BasePrimitiveReader.short

        @TestFactory
        fun `Testing reader for 'Short' type`(): Collection<DynamicTest> =
            listOf(
                "min value" to Short.MIN_VALUE,
                "max value" to Short.MAX_VALUE
            ).map { (displayName: String, value: Short) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(input)

                    result as JsResult.Success
                    assertEquals(value, result.value)
                    assertEquals(JsPath.empty, result.path)
                }
            }

        @Test
        fun `Testing reader for 'Short' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }

        @Test
        fun `Testing reader for 'Short' type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals(Long.MAX_VALUE.toString(), error.value)
            assertEquals(Short::class, error.type)
        }

        @Test
        fun `Testing reader for 'Short' type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals("10.5", error.value)
            assertEquals(Short::class, error.type)
        }
    }

    @Nested
    inner class IntReader {
        val reader = BasePrimitiveReader.int

        @TestFactory
        fun `Testing reader for 'Int' type`(): Collection<DynamicTest> =
            listOf(
                "min value" to Int.MIN_VALUE,
                "max value" to Int.MAX_VALUE
            ).map { (displayName: String, value: Int) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(input)

                    result as JsResult.Success
                    assertEquals(value, result.value)
                    assertEquals(JsPath.empty, result.path)
                }
            }

        @Test
        fun `Testing reader for 'Int' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }

        @Test
        fun `Testing reader for 'Int' type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals(Long.MAX_VALUE.toString(), error.value)
            assertEquals(Int::class, error.type)
        }

        @Test
        fun `Testing reader for 'Int' type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals("10.5", error.value)
            assertEquals(Int::class, error.type)
        }
    }

    @Nested
    inner class LongReader {
        val reader = BasePrimitiveReader.long

        @TestFactory
        fun `Testing reader for 'Long' type`(): Collection<DynamicTest> =
            listOf(
                "min value" to Long.MIN_VALUE,
                "max value" to Long.MAX_VALUE
            ).map { (displayName: String, value: Long) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(input)

                    result as JsResult.Success
                    assertEquals(value, result.value)
                    assertEquals(JsPath.empty, result.path)
                }
            }

        @Test
        fun `Testing reader for 'Long' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }

        @Test
        fun `Testing reader for 'Long' type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidNumber
            assertEquals("10.5", error.value)
            assertEquals(Long::class, error.type)
        }
    }

    @Nested
    inner class BigDecimalReader {
        val reader = BasePrimitiveReader.bigDecimal

        @TestFactory
        fun `Testing reader for 'BigDecimal' type`(): Collection<DynamicTest> =
            listOf(
                "min value" to BigDecimal.valueOf(Long.MIN_VALUE),
                "max value" to BigDecimal.valueOf(Long.MAX_VALUE)
            ).map { (displayName: String, value: BigDecimal) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value.toPlainString())!!

                    val result = reader.read(input)

                    result as JsResult.Success
                    assertEquals(value, result.value)
                    assertEquals(JsPath.empty, result.path)
                }
            }

        @Test
        fun `Testing reader for 'BigDecimal' type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(input)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }
    }
}
