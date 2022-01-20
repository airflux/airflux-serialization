package io.github.airflux.core.reader.context

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsReaderContextTest {

    companion object {
        private val user = User()
        private val order = Order()
    }

    @Nested
    inner class Element {

        @Test
        fun `Testing method the plus for Element`() {

            val elements = user + order

            assertEquals(2, elements.size)
            assertTrue(user in elements)
            assertTrue(order in elements)
        }
    }

    @Nested
    inner class Constructor {
        @Test
        fun `Testing constructor of JsReaderContextInstance without elements`() {

            val context = JsReaderContext()

            assertTrue(context.isEmpty)
        }

        @Test
        fun `Testing constructor of JsReaderContextInstance with one element`() {

            val context = JsReaderContext(user)

            assertTrue(User.Key in context)
            assertEquals(user, context[User.Key])
        }

        @Test
        fun `Testing constructor of JsReaderContextInstance with collection elements`() {

            val context = JsReaderContext(listOf(user, order))

            assertTrue(User.Key in context)
            assertEquals(user, context[User.Key])

            assertTrue(User.Key in context)
            assertEquals(order, context[Order.Key])
        }
    }

    @Nested
    inner class EmptyContext {

        @Test
        fun `Testing method the plus`() {
            val context = JsReaderContext() + user

            assertEquals(user, context[User.Key])
        }

        @Test
        fun `Testing method the get`() {
            val context = JsReaderContext()

            assertNull(context[User.Key])
        }

        @Test
        fun `Testing method the contains`() {
            val context = JsReaderContext()

            assertFalse(User.Key in context)
        }

        @Test
        fun `Testing property the isEmpty`() {
            val context = JsReaderContext()

            assertTrue(context.isEmpty)
        }

        @Test
        fun `Testing property the isNotEmpty`() {
            val context = JsReaderContext()

            assertFalse(context.isNotEmpty)
        }
    }

    @Nested
    inner class NonEmptyContext {

        @Test
        fun `Testing method the plus`() {

            val context = JsReaderContext(user) + order

            assertEquals(user, context[User.Key])
            assertEquals(order, context[Order.Key])
        }

        @Test
        fun `Testing method the get`() {

            val context = JsReaderContext(user)

            assertEquals(user, context[User.Key])
            assertNull(context[Order.Key])
        }

        @Test
        fun `Testing method the contains`() {

            val context = JsReaderContext(user)

            assertTrue(User.Key in context)
            assertFalse(Order.Key in context)
        }

        @Test
        fun `Testing property the isEmpty`() {
            val context = JsReaderContext(user)

            assertFalse(context.isEmpty)
        }

        @Test
        fun `Testing property the isNotEmpty`() {
            val context = JsReaderContext(user)

            assertTrue(context.isNotEmpty)
        }
    }

    class User : JsReaderAbstractContextElement(Key) {
        companion object Key : JsReaderContext.Key<User>
    }

    class Order : JsReaderAbstractContextElement(Key) {
        companion object Key : JsReaderContext.Key<Order>
    }
}
