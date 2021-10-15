package io.github.airflux.reader.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import kotlin.test.Test

class BasePrimitiveReaderTest {

    companion object {
        private val context = JsReaderContext()
        private const val TITLE_MIN_VALUE = "min value"
        private const val TITLE_MAX_VALUE = "max value"
    }

    @Nested
    inner class BooleanReader {
        val reader = BasePrimitiveReader.boolean(JsonErrors::InvalidType)

        @Test
        fun `Testing reader for the Boolean type`() {
            val value = true
            val input: JsValue = JsBoolean.valueOf(value)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsSuccess(path = JsResultPath.Root, value = value)
        }

        @Test
        fun `Testing reader for the Boolean type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.BOOLEAN,
                    actual = JsValue.Type.STRING
                )
            )
        }
    }

    @Nested
    inner class StringReader {
        val reader = BasePrimitiveReader.string(JsonErrors::InvalidType)

        @Test
        fun `Testing reader for the String type`() {
            val value = "abc"
            val input: JsValue = JsString(value)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsSuccess(path = JsResultPath.Root, value = value)
        }

        @Test
        fun `Testing reader for the String type (reading from invalid node)`() {
            val input: JsValue = JsBoolean.valueOf(true)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.STRING,
                    actual = JsValue.Type.BOOLEAN
                )
            )
        }
    }

    @Nested
    inner class ByteReader {
        val reader = BasePrimitiveReader.byte(JsonErrors::InvalidType, JsonErrors::ValueCast)

        @TestFactory
        fun `Testing reader for the Byte type`(): Collection<DynamicTest> =
            listOf(
                TITLE_MIN_VALUE to Byte.MIN_VALUE,
                TITLE_MAX_VALUE to Byte.MAX_VALUE
            ).map { (displayName: String, value: Byte) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(context, JsResultPath.Root, input)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)
                }
            }

        @Test
        fun `Testing reader for the Byte type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.NUMBER,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing reader for the Byte type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Byte::class)
            )
        }

        @Test
        fun `Testing reader for the Byte type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = "10.5", type = Byte::class)
            )
        }
    }

    @Nested
    inner class ShortReader {
        val reader = BasePrimitiveReader.short(JsonErrors::InvalidType, JsonErrors::ValueCast)

        @TestFactory
        fun `Testing reader for the Short type`(): Collection<DynamicTest> =
            listOf(
                TITLE_MIN_VALUE to Short.MIN_VALUE,
                TITLE_MAX_VALUE to Short.MAX_VALUE
            ).map { (displayName: String, value: Short) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(context, JsResultPath.Root, input)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)
                }
            }

        @Test
        fun `Testing reader for the Short type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.NUMBER,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing reader for the Short type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Short::class)
            )
        }

        @Test
        fun `Testing reader for the Short type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = "10.5", type = Short::class)
            )
        }
    }

    @Nested
    inner class IntReader {
        val reader = BasePrimitiveReader.int(JsonErrors::InvalidType, JsonErrors::ValueCast)

        @TestFactory
        fun `Testing reader for the Int type`(): Collection<DynamicTest> =
            listOf(
                TITLE_MIN_VALUE to Int.MIN_VALUE,
                TITLE_MAX_VALUE to Int.MAX_VALUE
            ).map { (displayName: String, value: Int) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(context, JsResultPath.Root, input)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)
                }
            }

        @Test
        fun `Testing reader for the Int type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.NUMBER,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing reader for the Int type (reading a value that more the allowed range)`() {
            val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Int::class)
            )
        }

        @Test
        fun `Testing reader for the Int type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = "10.5", type = Int::class)
            )
        }
    }

    @Nested
    inner class LongReader {
        val reader = BasePrimitiveReader.long(JsonErrors::InvalidType, JsonErrors::ValueCast)

        @TestFactory
        fun `Testing reader for the Long type`(): Collection<DynamicTest> =
            listOf(
                TITLE_MIN_VALUE to Long.MIN_VALUE,
                TITLE_MAX_VALUE to Long.MAX_VALUE
            ).map { (displayName: String, value: Long) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value)

                    val result = reader.read(context, JsResultPath.Root, input)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)
                }
            }

        @Test
        fun `Testing reader for the Long type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.NUMBER,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing reader for the Long type (reading a value of an invalid format)`() {
            val input: JsValue = JsNumber.valueOf("10.5")!!

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.ValueCast(value = "10.5", type = Long::class)
            )
        }
    }

    @Nested
    inner class BigDecimalReader {
        val reader = BasePrimitiveReader.bigDecimal(JsonErrors::InvalidType)

        @TestFactory
        fun `Testing reader for the BigDecimal type`(): Collection<DynamicTest> =
            listOf(
                TITLE_MIN_VALUE to BigDecimal.valueOf(Long.MIN_VALUE),
                TITLE_MAX_VALUE to BigDecimal.valueOf(Long.MAX_VALUE)
            ).map { (displayName: String, value: BigDecimal) ->
                DynamicTest.dynamicTest(displayName) {
                    val input: JsValue = JsNumber.valueOf(value.toPlainString())!!

                    val result = reader.read(context, JsResultPath.Root, input)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)

                    result.assertAsSuccess(path = JsResultPath.Root, value = value)
                }
            }

        @Test
        fun `Testing reader for the BigDecimal type (reading from invalid node)`() {
            val input: JsValue = JsString("abc")

            val result = reader.read(context, JsResultPath.Root, input)

            result.assertAsFailure(
                JsResultPath.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.NUMBER,
                    actual = JsValue.Type.STRING
                )
            )
        }
    }
}
