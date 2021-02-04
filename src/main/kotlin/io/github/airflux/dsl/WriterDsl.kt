package io.github.airflux.dsl

import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsWriter
import io.github.airflux.writer.extension.writeNullableAttribute
import io.github.airflux.writer.extension.writeOptionalAttribute
import io.github.airflux.writer.extension.writeOptionalTraversableAttribute
import io.github.airflux.writer.extension.writeRequiredAttribute
import io.github.airflux.writer.extension.writeTraversableAttribute
import kotlin.reflect.KProperty1

@Suppress("unused")
object WriterDsl {

    fun <T : Any> objectWriter(block: Builder<T>.() -> Unit): JsWriter<T> {
        val configuration = BuilderInstance<T>().apply { block() }
        return JsWriter { input ->
            mutableMapOf<String, JsValue>()
                .apply {
                    configuration.objectMap
                        .forEach { (name, transformer) ->
                            transformer(input)?.also { this[name] = it }
                        }
                }
                .let { JsObject(it) }
        }
    }

    interface Builder<T> {

        fun <R : Any> writeRequired(from: KProperty1<T, R>, to: String? = null, using: JsWriter<R>)

        fun <R : Any> writeOptional(from: KProperty1<T, R?>, to: String? = null, using: JsWriter<R>)

        fun <R : Any> writeNullable(from: KProperty1<T, R?>, to: String? = null, using: JsWriter<R>)

        fun <R : Any, C : Collection<R>> writeTraversable(
            from: KProperty1<T, C>,
            to: String? = null,
            using: JsWriter<R>
        )

        fun <R : Any, C : Collection<R>> writeOptionalTraversable(
            from: KProperty1<T, C>,
            to: String? = null,
            using: JsWriter<R>
        )
    }

    private class BuilderInstance<T> : Builder<T> {

        val objectMap = mutableMapOf<String, (T) -> JsValue?>()

        override fun <R : Any> writeRequired(from: KProperty1<T, R>, to: String?, using: JsWriter<R>) {
            val attributeName = to ?: from.name
            objectMap[attributeName] = writeRequiredAttribute(from = from, using = using)
        }

        override fun <R : Any> writeOptional(from: KProperty1<T, R?>, to: String?, using: JsWriter<R>) {
            val attributeName = to ?: from.name
            objectMap[attributeName] = writeOptionalAttribute(from = from, using = using)
        }

        override fun <R : Any> writeNullable(from: KProperty1<T, R?>, to: String?, using: JsWriter<R>) {
            val attributeName = to ?: from.name
            objectMap[attributeName] = writeNullableAttribute(from = from, using = using)
        }

        override fun <R : Any, C : Collection<R>> writeTraversable(
            from: KProperty1<T, C>,
            to: String?,
            using: JsWriter<R>
        ) {
            val attributeName = to ?: from.name
            objectMap[attributeName] = writeTraversableAttribute(from = from, using = using)
        }

        override fun <R : Any, C : Collection<R>> writeOptionalTraversable(
            from: KProperty1<T, C>,
            to: String?,
            using: JsWriter<R>
        ) {
            val attributeName = to ?: from.name
            objectMap[attributeName] = writeOptionalTraversableAttribute(from = from, using = using)
        }
    }
}

