package io.github.airflux.dsl

import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsWriter

@Suppress("unused")
object WriterDsl2 {

    fun <T : Any> objectWriter(block: Builder.(T) -> Unit) =
        JsWriter<T> { input ->
            BuilderInstance()
                .apply { block(input) }
                .build()
        }

    interface Builder {
        fun <R : Any> writeRequired(value: R, to: String, using: JsWriter<R>)

        fun <R : Any> writeOptional(value: R?, to: String, using: JsWriter<R>)

        fun <R : Any> writeNullable(value: R?, to: String, using: JsWriter<R>)

        fun <R : Any, C : Collection<R>> writeTraversable(values: C, to: String, using: JsWriter<R>)
    }

    private class BuilderInstance : Builder {

        private val objectMap = mutableMapOf<String, JsValue>()

        override fun <R : Any> writeRequired(value: R, to: String, using: JsWriter<R>) {
            objectMap[to] = using.write(value)
        }

        override fun <R : Any> writeOptional(value: R?, to: String, using: JsWriter<R>) {
            if (value != null)
                objectMap[to] = using.write(value)
        }

        override fun <R : Any> writeNullable(value: R?, to: String, using: JsWriter<R>) {
            if (value != null)
                objectMap[to] = using.write(value)
            else
                objectMap[to] = JsNull
        }

        override fun <R : Any, C : Collection<R>> writeTraversable(values: C, to: String, using: JsWriter<R>) {
            objectMap[to] = JsArray(values.map { value -> using.write(value) })
        }

        fun build() = JsObject(objectMap)
    }
}
