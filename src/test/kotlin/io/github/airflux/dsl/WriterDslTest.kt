package io.github.airflux.dsl

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.TestData.USER_ROLE_VALUE
import io.github.airflux.dsl.WriterDsl.objectWriter
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.BasePrimitiveWriter
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class WriterDslTest {

    @Nested
    inner class WriteRequired {

        inner class User(val name: String)

        @Test
        fun `Testing of a write of a required attribute`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> {
                writeRequired(from = User::name, to = "name", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of a required attribute (an attribute name is the property name)`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> {
                writeRequired(from = User::name, using = BasePrimitiveWriter.string)
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

        inner class User(val role: String?)

        @Test
        fun `Testing of a write of an optional attribute`() {
            val value = User(role = USER_ROLE_VALUE)
            val writer = objectWriter<User> {
                writeOptional(from = User::role, to = "other", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["other"] as JsString
            assertEquals(USER_ROLE_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of an optional attribute (an attribute name is the property name)`() {
            val value = User(role = USER_ROLE_VALUE)
            val writer = objectWriter<User> {
                writeOptional(from = User::role, using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["role"] as JsString
            assertEquals(USER_ROLE_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of an optional attribute (a value of an attribute is null)`() {
            val value = User(role = null)
            val writer = objectWriter<User> {
                writeOptional(from = User::role, to = "name", using = BasePrimitiveWriter.string)
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
        fun `Testing of a write of a nullable attribute`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> {
                writeNullable(from = User::name, to = "other", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["other"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of a nullable attribute (an attribute name is the property name)`() {
            val value = User(name = USER_NAME_VALUE)
            val writer = objectWriter<User> {
                writeNullable(from = User::name, using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of a nullable attribute (a value of an attribute is null)`() {
            val value = User(name = null)
            val writer = objectWriter<User> {
                writeNullable(from = User::name, to = "other", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            assertEquals(JsNull, result.underlying["other"])
        }
    }

    @Nested
    inner class WriteTraversable {

        inner class User(val phones: List<String>)

        @Test
        fun `Testing of a write of a traversable attribute`() {
            val value = User(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
            val writer = objectWriter<User> {
                writeTraversable(from = User::phones, to = "others", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val phones = result.underlying["others"] as JsArray<*>
            assertEquals(2, phones.underlying.size)

            val firstPhone = phones.underlying[0] as JsString
            assertEquals(FIRST_PHONE_VALUE, firstPhone.underlying)
            val secondPhone = phones.underlying[1] as JsString
            assertEquals(SECOND_PHONE_VALUE, secondPhone.underlying)
        }

        @Test
        fun `Testing of a write of a traversable attribute (an attribute name is the property name)`() {
            val value = User(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
            val writer = objectWriter<User> {
                writeTraversable(from = User::phones, using = BasePrimitiveWriter.string)
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
        fun `Testing of a write of a traversable attribute (an attribute is empty)`() {
            val value = User(phones = emptyList())
            val writer = objectWriter<User> {
                writeTraversable(from = User::phones, to = "others", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val phones = result.underlying["others"] as JsArray<*>
            assertEquals(0, phones.underlying.size)
        }
    }

    @Nested
    inner class WriteOptionalTraversable {

        inner class User(val phones: List<String>)

        @Test
        fun `Testing of a write of a optional traversable attribute`() {
            val value = User(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
            val writer = objectWriter<User> {
                writeOptionalTraversable(from = User::phones, to = "others", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val phones = result.underlying["others"] as JsArray<*>
            assertEquals(2, phones.underlying.size)

            val firstPhone = phones.underlying[0] as JsString
            assertEquals(FIRST_PHONE_VALUE, firstPhone.underlying)
            val secondPhone = phones.underlying[1] as JsString
            assertEquals(SECOND_PHONE_VALUE, secondPhone.underlying)
        }

        @Test
        fun `Testing of a write of a optional traversable attribute (an attribute name is the property name)`() {
            val value = User(phones = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
            val writer = objectWriter<User> {
                writeOptionalTraversable(from = User::phones, using = BasePrimitiveWriter.string)
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
        fun `Testing of a write of a optional traversable attribute (an attribute is empty)`() {
            val value = User(phones = emptyList())
            val writer = objectWriter<User> {
                writeOptionalTraversable(from = User::phones, to = "others", using = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(0, result.underlying.size)
        }
    }
}
