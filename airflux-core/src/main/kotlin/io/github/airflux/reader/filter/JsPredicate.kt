package io.github.airflux.reader.filter

import io.github.airflux.reader.context.JsReaderContext

@Suppress("unused")
fun interface JsPredicate<T> {

    fun test(context: JsReaderContext?, value: T): Boolean

    infix fun or(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, value ->
            val result = self.test(context, value)
            if (result) result else other.test(context, value)
        }
    }

    infix fun and(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, value ->
            val result = self.test(context, value)
            if (result) other.test(context, value) else result
        }
    }
}
