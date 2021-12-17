package io.github.airflux.reader.filter

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsLocation
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class JsPredicateTest {

    companion object {
        private val context = JsReaderContext()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "5:false",
            "10:false",
            "15:true",
            "20:false",
            "25:false",
        ],
        delimiter = ':'
    )
    fun `Testing of the logical operator the and of a validator`(actual: Int, expected: Boolean) {
        val leftFilter = JsPredicate<Int> { _, _, value -> value > 10 }
        val rightFilter = JsPredicate<Int> { _, _, value -> value < 20 }
        val composedFilter = leftFilter and rightFilter
        assertEquals(expected, composedFilter.test(context, JsLocation.Root, actual))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "5:true",
            "10:false",
            "15:false",
            "20:false",
            "25:true",
        ],
        delimiter = ':'
    )
    fun `Testing of the logical operator the or of a validator`(actual: Int, expected: Boolean) {
        val leftFilter = JsPredicate<Int> { _, _, value -> value < 10 }
        val rightFilter = JsPredicate<Int> { _, _, value -> value > 20 }
        val composedFilter = leftFilter or rightFilter
        assertEquals(expected, composedFilter.test(context, JsLocation.Root, actual))
    }
}
