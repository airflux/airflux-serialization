package io.github.airflux.reader.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class JsResultPathTest {

    @Test
    fun `Testing 'equals contract`() {
        assertEquals(
            JsResultPath.Root,
            JsResultPath.Root
        )
        assertEquals(
            JsResultPath.Root / "user",
            JsResultPath.Root / "user"
        )
        assertEquals(
            JsResultPath.Root / "user" / "address",
            JsResultPath.Root / "user" / "address"
        )
        assertEquals(
            JsResultPath.Root / "user" / "phones" / 0,
            JsResultPath.Root / "user" / "phones" / 0
        )

        assertNotEquals(JsResultPath.Root, Any())
        assertNotEquals(Any(), JsResultPath.Root)

        assertNotEquals(
            JsResultPath.Root,
            JsResultPath.Root / "user"
        )
        assertNotEquals(
            JsResultPath.Root / "user",
            JsResultPath.Root
        )

        assertNotEquals(
            JsResultPath.Root / "user",
            JsResultPath.Root / 0
        )
        assertNotEquals(
            JsResultPath.Root / 0,
            JsResultPath.Root / "user"
        )

        assertNotEquals(
            JsResultPath.Root / "user" / 0,
            JsResultPath.Root / "user"
        )
        assertNotEquals(
            JsResultPath.Root / "user",
            JsResultPath.Root / "user" / 0
        )
    }

    @Test
    fun `Testing 'hashCode contract`() {
        assertEquals(
            JsResultPath.Root.hashCode(),
            JsResultPath.Root.hashCode()
        )
        assertEquals(
            (JsResultPath.Root / "user").hashCode(),
            (JsResultPath.Root / "user").hashCode()
        )
        assertEquals(
            (JsResultPath.Root / "user" / "address").hashCode(),
            (JsResultPath.Root / "user" / "address").hashCode()
        )
        assertEquals(
            (JsResultPath.Root / "user" / "address" / "city").hashCode(),
            (JsResultPath.Root / "user" / "address" / "city").hashCode()
        )
        assertEquals(
            (JsResultPath.Root / "user" / 0).hashCode(),
            (JsResultPath.Root / "user" / 0).hashCode()
        )
        assertEquals(
            (JsResultPath.Root / "user" / 0 / 0).hashCode(),
            (JsResultPath.Root / "user" / 0 / 0).hashCode()
        )

        assertNotEquals(
            Any().hashCode(),
            JsResultPath.Root.hashCode()
        )
        assertNotEquals(
            JsResultPath.Root.hashCode(),
            (JsResultPath.Root / "user").hashCode()
        )
        assertNotEquals(
            (JsResultPath.Root / "user").hashCode(),
            (JsResultPath.Root / 0).hashCode()
        )
        assertNotEquals(
            (JsResultPath.Root / "user").hashCode(),
            (JsResultPath.Root / "user" / 0).hashCode()
        )
        assertNotEquals(
            (JsResultPath.Root / "user" / 0).hashCode(),
            (JsResultPath.Root / "user" / 0 / 0).hashCode(),
        )
    }

    @Test
    fun `Testing 'toString contract`() {
        assertEquals("#", JsResultPath.Root.toString())
        assertEquals("#/user", (JsResultPath.Root / "user").toString())
        assertEquals("#/user/phones", (JsResultPath.Root / "user" / "phones").toString())
        assertEquals("#/user/phones[0]", (JsResultPath.Root / "user" / "phones" / 0).toString())
    }
}
