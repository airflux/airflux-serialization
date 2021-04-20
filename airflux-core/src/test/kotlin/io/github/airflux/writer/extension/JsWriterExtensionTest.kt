package io.github.airflux.writer.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
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

    @Nested
    inner class WriteRequiredProperty {

        inner class A(val name: String)

        private val writer = writeRequiredProperty(from = A::name, using = BasePrimitiveWriter.string)

        @Test
        fun `Testing of a write of a required property`() {
            val value = A(name = USER_NAME_VALUE)

            val result = writer(value)

            result as JsString
            assertEquals(USER_NAME_VALUE, result.underlying)
        }
    }

    @Nested
    inner class WriteOptionalProperty {
        inner class A(val name: String?)

        private val writer = writeOptionalProperty(from = A::name, using = BasePrimitiveWriter.string)

        @Test
        fun `Testing of a write of an optional property`() {
            val value = A(name = USER_NAME_VALUE)

            val result = writer(value)

            result as JsString
            assertEquals(USER_NAME_VALUE, result.underlying)
        }

        @Test
        fun `Testing of a write of an optional property (a value of a property is null)`() {
            val value = A(name = null)

            val result = writer(value)

            assertNull(result)
        }
    }

    @Nested
    inner class WriteNullableProperty {
        inner class A(val name: String?)

        private val writer = writeNullableProperty(from = A::name, using = BasePrimitiveWriter.string)

        @Test
        fun `Testing of a write of a nullable property`() {
            val value = A(name = USER_NAME_VALUE)

            val result = writer(value)

            result as JsString
            assertEquals(USER_NAME_VALUE, result.underlying)
        }

        @Test
        fun `Testing of a write of a nullable property (a value of a property is null)`() {
            val value = A(name = null)

            val result = writer(value)

            assertTrue(result is JsNull)
        }
    }

    @Nested
    inner class WriteTraversableProperty {

        inner class A(val phones: List<String>)

        private val writer = writeTraversableProperty(from = A::phones, using = BasePrimitiveWriter.string)

        @Test
        fun `Testing of a write of a traversable property`() {
            val value = A(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))

            val result = writer(value)

            result as JsArray<*>
            assertEquals(2, result.underlying.size)

            val firstPhoneNumber = result.underlying[0] as JsString
            assertEquals(FIRST_PHONE_VALUE, firstPhoneNumber.underlying)
            val secondPhoneNumber = result.underlying[1] as JsString
            assertEquals(SECOND_PHONE_VALUE, secondPhoneNumber.underlying)
        }

        @Test
        fun `Testing of a write of a traversable property (a property is empty)`() {
            val value = A(phones = emptyList())

            val result = writer(value)

            result as JsArray<*>
            assertEquals(0, result.underlying.size)
        }
    }

    @Nested
    inner class WriteOptionalTraversableProperty {

        inner class A(val phones: List<String>)

        private val writer = writeOptionalTraversableProperty(from = A::phones, using = BasePrimitiveWriter.string)

        @Test
        fun `Testing of a write of a optional traversable property`() {
            val value = A(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))

            val result = writer(value)

            result as JsArray<*>
            assertEquals(2, result.underlying.size)

            val firstPhoneNumber = result.underlying[0] as JsString
            assertEquals(FIRST_PHONE_VALUE, firstPhoneNumber.underlying)
            val secondPhoneNumber = result.underlying[1] as JsString
            assertEquals(SECOND_PHONE_VALUE, secondPhoneNumber.underlying)
        }

        @Test
        fun `Testing of a write of a optional traversable property (a property is empty)`() {
            val value = A(phones = emptyList())

            val result = writer(value)

            assertNull(result)
        }
    }
}
