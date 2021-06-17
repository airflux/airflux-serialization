package io.github.airflux.reader.filter

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath

@Suppress("unused")
fun interface JsPredicate<T> {

    fun test(context: JsReaderContext?, path: JsResultPath, value: T): Boolean

    infix fun or(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, path, value ->
            val result = self.test(context, path, value)
            if (result) result else other.test(context, path, value)
        }
    }

    infix fun and(other: JsPredicate<T>): JsPredicate<T> {
        val self = this
        return JsPredicate { context, path, value ->
            val result = self.test(context, path, value)
            if (result) other.test(context, path, value) else result
        }
    }
}
