package io.github.airflux.writer.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.BasePrimitiveWriter
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsWriterExtensionTest {

    class User(val firstName: String, val phoneNumber: String? = null)

    @Nested
    inner class WriteAsRequired {

        @Test
        fun `Testing writeAsRequired function`() {
            val value = User(firstName = USER_NAME_VALUE)

            val result = writeAsRequired(value, User::firstName, BasePrimitiveWriter.string)

            result as JsString
            assertEquals(USER_NAME_VALUE, result.underlying)
        }
    }

    @Nested
    inner class WriteAsOptional {

        @Test
        fun `Testing writeAsOptional function`() {
            val value = User(firstName = USER_NAME_VALUE, phoneNumber = FIRST_PHONE_VALUE)

            val result = writeAsOptional(value, User::phoneNumber, BasePrimitiveWriter.string)

            result as JsString
            assertEquals(FIRST_PHONE_VALUE, result.underlying)
        }

        @Test
        fun `Testing writeAsOptional function (a value of a property is null)`() {
            val value = User(firstName = USER_NAME_VALUE)

            val result = writeAsOptional(value, User::phoneNumber, BasePrimitiveWriter.string)

            assertNull(result)
        }
    }

    @Nested
    inner class WriteAsNullable {

        @Test
        fun `Testing writeAsNullable function`() {
            val value = User(firstName = USER_NAME_VALUE, phoneNumber = FIRST_PHONE_VALUE)

            val result = writeAsNullable(value, User::phoneNumber, BasePrimitiveWriter.string)

            result as JsString
            assertEquals(FIRST_PHONE_VALUE, result.underlying)
        }

        @Test
        fun `Testing writeAsNullable function (a value of a property is null)`() {
            val value = User(firstName = USER_NAME_VALUE)

            val result = writeAsNullable(value, User::phoneNumber, BasePrimitiveWriter.string)

            assertTrue(result is JsNull)
        }
    }

    @Nested
    inner class ArrayWriter {

        @Suppress("UNCHECKED_CAST")
        @Test
        fun `Testing arrayWriter function`() {
            val writer = arrayWriter(BasePrimitiveWriter.string)
            val value = listOf("One", "Two")

            val result = writer.write(value)

            result as JsArray<JsString>
            assertEquals("One", result.underlying[0].underlying)
            assertEquals("Two", result.underlying[1].underlying)
        }
    }
}
