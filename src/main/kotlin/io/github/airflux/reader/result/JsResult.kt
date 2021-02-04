package io.github.airflux.reader.result

import io.github.airflux.path.JsPath

@Suppress("unused")
sealed class JsResult<out T> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R> = when (this) {
        is Success -> Success(transform(value), path)
        is Failure -> this
    }

    fun <R> flatMap(transform: (T) -> JsResult<R>): JsResult<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    inline infix fun onFailure(block: (Failure) -> Nothing): T = when (this) {
        is Success<T> -> value
        is Failure -> block(this)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    abstract infix fun repath(path: JsPath): JsResult<T>

    class Success<T>(val value: T, val path: JsPath = JsPath()) : JsResult<T>() {

        override fun repath(path: JsPath): JsResult<T> = Success(value, path + this.path)
    }

    class Failure(val errors: List<Pair<JsPath, List<JsError>>>) : JsResult<Nothing>() {

        constructor() : this(JsPath(), emptyList())

        constructor(error: JsError) : this(JsPath(), listOf(error))

        constructor(path: JsPath, error: JsError) : this(path, listOf(error))

        constructor(path: JsPath, errors: List<JsError>) : this(listOf(Pair(path, errors)))

        override fun repath(path: JsPath): JsResult<Nothing> =
            Failure(errors = errors.map { (oldPath, errors) -> path + oldPath to errors })
    }
}
