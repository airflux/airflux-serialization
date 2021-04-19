package io.github.airflux.reader.result

import io.github.airflux.path.JsPath

@Suppress("unused")
sealed class JsResult<out T, out E : JsError> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R, E> = when (this) {
        is Success -> Success(transform(value), path)
        is Failure -> this
    }

    fun <R> flatMap(transform: (T) -> JsResult<R, @UnsafeVariance E>): JsResult<R, E> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    inline infix fun onFailure(block: (Failure<@UnsafeVariance E>) -> Nothing): T = when (this) {
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

    abstract infix fun repath(path: JsPath): JsResult<T, E>

    class Success<T>(val value: T, val path: JsPath = JsPath.empty) : JsResult<T, Nothing>() {

        override fun repath(path: JsPath): JsResult<T, Nothing> = Success(value, path + this.path)
    }

    class Failure<out E : JsError>(val errors: List<Pair<JsPath, List<E>>>) : JsResult<Nothing, E>() {

        constructor() : this(JsPath.empty, emptyList())

        constructor(error: E) : this(JsPath.empty, listOf(error))

        constructor(path: JsPath, error: E) : this(path, listOf(error))

        constructor(path: JsPath, errors: List<E>) : this(listOf(Pair(path, errors)))

        override fun repath(path: JsPath): JsResult<Nothing, E> =
            Failure(errors = errors.map { (oldPath, errors) -> path + oldPath to errors })
    }
}

fun <T> T.asSuccess(path: JsPath = JsPath.empty): JsResult<T, Nothing> =
    JsResult.Success(value = this, path = path)

fun <E : JsError> E.asFailure(path: JsPath = JsPath.empty): JsResult<Nothing, E> =
    JsResult.Failure(path = path, error = this)

fun <E : JsError> List<E>.asFailure(path: JsPath = JsPath.empty): JsResult<Nothing, E> =
    JsResult.Failure(path = path, errors = this)
