package io.github.airflux.dsl

import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.BasePrimitiveWriter
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class WriterDslTest {

    companion object {
        val objectWriter = ObjectWriter(ObjectWriterConfiguration())
    }

    @Nested
    inner class WriteRequired {

        inner class User(val name: String)

        @Test
        fun `Testing of a write of a required property`() {
            val writer = objectWriter<User> {
                requiredProperty(name = "name", from = User::name, writer = BasePrimitiveWriter.string)
            }
            val value = User(name = USER_NAME_VALUE)

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
            val writer = objectWriter<User> {
                optionalProperty(name = "name", from = User::name, writer = BasePrimitiveWriter.string)
            }
            val value = User(name = USER_NAME_VALUE)

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            val name = result.underlying["name"] as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing of a write of an optional property (a value of a property is null)`() {
            val value = User(name = null)
            val writer = objectWriter<User> {
                optionalProperty(name = "name", from = User::name, writer = BasePrimitiveWriter.string)
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
            val writer = objectWriter<User> {
                nullableProperty(name = "name", from = User::name, writer = BasePrimitiveWriter.string)
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
            val writer = objectWriter<User> {
                nullableProperty(name = "name", from = User::name, writer = BasePrimitiveWriter.string)
            }

            val result = writer.write(value)

            result as JsObject
            assertEquals(1, result.underlying.size)
            assertEquals(JsNull, result.underlying["name"])
        }
    }
}
