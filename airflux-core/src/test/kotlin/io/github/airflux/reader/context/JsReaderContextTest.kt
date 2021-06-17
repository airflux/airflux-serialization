package io.github.airflux.reader.context

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsReaderContextTest {
    companion object {
        private const val USER_ID = "user-1"
        private val user = User(USER_ID)

        private val emptyContext = JsReaderContext()
        private val contextWithUser = JsReaderContext() + user
    }

    @Test
    fun `Testing context wth user`() {
        assertTrue(User.Key in contextWithUser)
        assertEquals(USER_ID, contextWithUser[User.Key]!!.id)
    }

    @Test
    fun `Testing empty context`() {
        assertFalse(User.Key in emptyContext)
        assertNull(emptyContext[User.Key])
    }

    class User(val id: String) : AbstractContextElement(Key) {
        companion object Key : JsReaderContext.Element.Key<User>
    }
}
