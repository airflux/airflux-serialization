package io.github.airflux.reader.result.fx

//import io.github.airflux.reader.result.JsResult
//
//fun <T> JsResult.Companion.fx(block: JsResultBinder.() -> T): JsResult<T> = JsResultBinderImpl().run(block)
//
//interface JsResultBinder {
//    fun <T> JsResult<T>.bind(): T
//
//    operator fun <T> JsResult<T>.component1(): T
//}
//
//private class JsResultBinderImpl : JsResultBinder {
//
//    private class BindingException(val error: JsResult.Failure) : RuntimeException()
//
//    override fun <T> JsResult<T>.bind(): T = when (this) {
//        is JsResult.Success -> this.value
//        is JsResult.Failure -> throw BindingException(this)
//    }
//
//    override fun <T> JsResult<T>.component1(): T = this.bind()
//
//    fun <T> run(block: JsResultBinder.() -> T): JsResult<T> = try {
//        JsResult.Success(block())
//    } catch (expected: BindingException) {
//        expected.error
//    }
//}
