package io.github.airflux.path.extension

import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import kotlin.test.Test
import kotlin.test.assertEquals

class JsPathExtensionTest {

    @Test
    fun twoKeyPathElement() {
        val path = "user" / "name"

        assertEquals(expected = 2, actual = path.elements.size)

        val firstElement = path.elements[0] as KeyPathElement
        assertEquals(expected = "user", actual = firstElement.key)

        val secondElement = path.elements[1] as KeyPathElement
        assertEquals(expected = "name", actual = secondElement.key)
    }

    @Test
    fun keyAndIdxPathElements() {
        val path = "phones" / 0

        assertEquals(expected = 2, actual = path.elements.size)

        val firstElement = path.elements[0] as KeyPathElement
        assertEquals(expected = "phones", actual = firstElement.key)

        val secondElement = path.elements[1] as IdxPathElement
        assertEquals(expected = 0, actual = secondElement.idx)
    }
}
