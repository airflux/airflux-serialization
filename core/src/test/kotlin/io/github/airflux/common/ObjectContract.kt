package io.github.airflux.common

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal object ObjectContract {

    fun <T : Any> checkEqualsContract(original: T, copy: T, other: T) {
        assertEquals(original, original)
        assertEquals(original, copy)
        assertEquals(copy, original)
        assertNotEquals(original, other)
        assertNotEquals(original, Any())

        assertEquals(original.hashCode(), original.hashCode())
        assertEquals(original.hashCode(), copy.hashCode())
        assertNotEquals(original.hashCode(), other.hashCode())
    }

    fun <T : Any> checkToString(value: T, expected: String) {
        assertEquals(expected = expected, actual = value.toString())
    }
}
