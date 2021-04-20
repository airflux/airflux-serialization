package io.github.airflux.dsl

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.dsl.WriterDsl2.objectWriter
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.BasePrimitiveWriter
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class WriterDsl2Test {

    @Nested
    inner class WriteRequired {

        inner class A(val name: String)

        @Test
        fun `Testing of a write of a required property`() {
            val value = A(name = USER_NAME_VALUE)
            val writer = objectWriter<A> { target ->
                writeRequired(value = target.name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }
    }

    @Nested
    inner class WriteOptional {

        inner class User(val name: String?)

        @Test
        fun `Testing of a write of an optional property`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> { target ->
                writeOptional(value = target.name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of an optional property (a value of a property is null)`() {
            val value = User(name = null)
            val writer = objectWriter<User> { target ->
                writeOptional(value = target.name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            val expected = JsObject()
            assertEquals(expected, result)
        }
    }

    @Nested
    inner class WriteNullable {

        inner class User(val name: String?)

        @Test
        fun `Testing of a write of a nullable property`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> { target ->
                writeNullable(value = target.name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of a nullable property (a value of a property is null)`() {
            val value = User(name = null)
            val writer = objectWriter<User> { target ->
                writeNullable(value = target.name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            assertEquals(JsNull, result.underlying["name"])
        }
    }

    @Nested
    inner class WriteTraversable {

        inner class User(val phones: List<String>)

        @Test
        fun `Testing of a write of a traversable property`() {
            val value = User(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
            val writer = objectWriter<User> { target ->
                writeTraversable(values = target.phones, to = "phones", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val phones = result.underlying["phones"] as JsArray<*>
            assertEquals(2, phones.underlying.size)

            val firstPhone = phones.underlying[0] as JsString
            assertEquals(FIRST_PHONE_VALUE, firstPhone.underlying)
            val secondPhone = phones.underlying[1] as JsString
            assertEquals(SECOND_PHONE_VALUE, secondPhone.underlying)
        }

        @Test
        fun `Testing of a write of a traversable property (a property is empty)`() {
            val value = User(phones = emptyList())
            val writer = objectWriter<User> { target ->
                writeTraversable(values = target.phones, to = "phones", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val phones = result.underlying["phones"] as JsArray<*>
            assertEquals(0, phones.underlying.size)
        }
    }
}
