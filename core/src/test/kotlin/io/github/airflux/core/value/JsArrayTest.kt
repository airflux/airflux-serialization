package io.github.airflux.core.value

import io.github.airflux.core.common.ObjectContract
import io.github.airflux.core.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.core.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.core.path.PathElement
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsArrayTest {

    companion object {
        private val EMPTY_ARRAY = JsArray<JsString>()
        private val FIRST_ITEM = JsString(FIRST_PHONE_VALUE)
        private val SECOND_ITEM = JsString(SECOND_PHONE_VALUE)
        private val NOT_EMPTY_ARRAY = JsArray(FIRST_ITEM, SECOND_ITEM)
    }

    @Test
    fun isEmpty() {
        assertTrue(EMPTY_ARRAY.isEmpty())
    }

    @Test
    fun isNotEmpty() {
        assertFalse(NOT_EMPTY_ARRAY.isEmpty())
    }

    @Test
    fun sizeEmptyArray() {
        assertEquals(0, EMPTY_ARRAY.size)
    }

    @Test
    fun sizeNotEmptyArray() {
        assertEquals(2, NOT_EMPTY_ARRAY.size)
    }

    @Test
    fun getByIntFromEmptyArray() {
        assertNull(EMPTY_ARRAY[0])
    }

    @Test
    fun getByIntFromNotEmptyArray() {
        val item = NOT_EMPTY_ARRAY[0]

        assertNotNull(item)
        item as JsString
        assertEquals(FIRST_PHONE_VALUE, item.get)
    }

    @Test
    fun getByIdxFromEmptyArray() {
        assertNull(EMPTY_ARRAY[PathElement.Idx(0)])
    }

    @Test
    fun getByIdxFromNotEmptyArray() {
        val item = NOT_EMPTY_ARRAY[PathElement.Idx(0)]

        assertNotNull(item)
        item as JsString
        assertEquals(FIRST_PHONE_VALUE, item.get)
    }

    @Test
    fun iterable() {
        assertContains(NOT_EMPTY_ARRAY, FIRST_ITEM)
        assertContains(NOT_EMPTY_ARRAY, SECOND_ITEM)
    }

    @Test
    fun `Testing the toString function of the JsArray class`() {
        ObjectContract.checkToString(
            JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsString(SECOND_PHONE_VALUE),
            ),
            """["$FIRST_PHONE_VALUE", "$SECOND_PHONE_VALUE"]"""
        )
    }

    @Test
    fun `Testing the equals contract of the JsString class`() {
        ObjectContract.checkEqualsContract(
            JsArray(JsString(FIRST_PHONE_VALUE)),
            JsArray(JsString(FIRST_PHONE_VALUE)),
            JsArray(JsString(SECOND_PHONE_VALUE)),
        )
    }
}
