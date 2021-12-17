package io.github.airflux.reader.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class JsLocationTest {

    @Test
    fun `Testing the equals contract`() {
        assertEquals(
            JsLocation.Root,
            JsLocation.Root
        )
        assertEquals(
            JsLocation.Root / "user",
            JsLocation.Root / "user"
        )
        assertEquals(
            JsLocation.Root / "user" / "address",
            JsLocation.Root / "user" / "address"
        )
        assertEquals(
            JsLocation.Root / "user" / "phones" / 0,
            JsLocation.Root / "user" / "phones" / 0
        )

        assertNotEquals(JsLocation.Root, Any())
        assertNotEquals(Any(), JsLocation.Root)

        assertNotEquals(
            JsLocation.Root,
            JsLocation.Root / "user"
        )
        assertNotEquals(
            JsLocation.Root / "user",
            JsLocation.Root
        )

        assertNotEquals(
            JsLocation.Root / "user",
            JsLocation.Root / 0
        )
        assertNotEquals(
            JsLocation.Root / 0,
            JsLocation.Root / "user"
        )

        assertNotEquals(
            JsLocation.Root / "user" / 0,
            JsLocation.Root / "user"
        )
        assertNotEquals(
            JsLocation.Root / "user",
            JsLocation.Root / "user" / 0
        )
    }

    @Test
    fun `Testing the hashCode contract`() {
        assertEquals(
            JsLocation.Root.hashCode(),
            JsLocation.Root.hashCode()
        )
        assertEquals(
            (JsLocation.Root / "user").hashCode(),
            (JsLocation.Root / "user").hashCode()
        )
        assertEquals(
            (JsLocation.Root / "user" / "address").hashCode(),
            (JsLocation.Root / "user" / "address").hashCode()
        )
        assertEquals(
            (JsLocation.Root / "user" / "address" / "city").hashCode(),
            (JsLocation.Root / "user" / "address" / "city").hashCode()
        )
        assertEquals(
            (JsLocation.Root / "user" / 0).hashCode(),
            (JsLocation.Root / "user" / 0).hashCode()
        )
        assertEquals(
            (JsLocation.Root / "user" / 0 / 0).hashCode(),
            (JsLocation.Root / "user" / 0 / 0).hashCode()
        )

        assertNotEquals(
            Any().hashCode(),
            JsLocation.Root.hashCode()
        )
        assertNotEquals(
            JsLocation.Root.hashCode(),
            (JsLocation.Root / "user").hashCode()
        )
        assertNotEquals(
            (JsLocation.Root / "user").hashCode(),
            (JsLocation.Root / 0).hashCode()
        )
        assertNotEquals(
            (JsLocation.Root / "user").hashCode(),
            (JsLocation.Root / "user" / 0).hashCode()
        )
        assertNotEquals(
            (JsLocation.Root / "user" / 0).hashCode(),
            (JsLocation.Root / "user" / 0 / 0).hashCode(),
        )
    }

    @Test
    fun `Testing the toString contract`() {
        assertEquals("#", JsLocation.Root.toString())
        assertEquals("#/user", (JsLocation.Root / "user").toString())
        assertEquals("#/user/phones", (JsLocation.Root / "user" / "phones").toString())
        assertEquals("#/user/phones[0]", (JsLocation.Root / "user" / "phones" / 0).toString())
    }
}
