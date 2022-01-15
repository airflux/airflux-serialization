package io.github.airflux.core.reader.predicate

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation

@Suppress("unused")
fun interface JsPredicate<T> {

    fun test(context: JsReaderContext, location: JsLocation, value: T): Boolean

    infix fun or(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, location, value ->
            val result = self.test(context, location, value)
            if (result) result else other.test(context, location, value)
        }
    }

    infix fun and(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, location, value ->
            val result = self.test(context, location, value)
            if (result) other.test(context, location, value) else result
        }
    }
}
