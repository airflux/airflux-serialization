package io.github.airflux.core.reader.predicate

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsPredicateAndCombinatorTest {

    companion object {
        private val context = JsReaderContext()

        private const val MIN_VALUE = 10
        private const val MAX_VALUE = 20

        private val leftFilter = JsPredicate<Int> { _, _, value -> value > MIN_VALUE }
        private val rightFilter = JsPredicate<Int> { _, _, value -> value < MAX_VALUE }
        private val composedFilter = leftFilter and rightFilter
    }

    @Test
    fun `The tested value is less to the minimum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MIN_VALUE - 1)
        assertFalse(result)
    }

    @Test
    fun `The tested value is equal to the minimum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MIN_VALUE)
        assertFalse(result)
    }

    @Test
    fun `The tested value is more to the minimum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MIN_VALUE + 1)
        assertTrue(result)
    }

    @Test
    fun `The tested value is less to the maximum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MAX_VALUE - 1)
        assertTrue(result)
    }

    @Test
    fun `The tested value is equal to the maximum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MAX_VALUE)
        assertFalse(result)
    }

    @Test
    fun `The tested value is more to the maximum value of the range`() {
        val result = composedFilter.test(context, JsLocation.empty, MAX_VALUE + 1)
        assertFalse(result)
    }
}
