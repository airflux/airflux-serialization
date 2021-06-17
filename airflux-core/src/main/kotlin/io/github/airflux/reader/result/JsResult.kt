package io.github.airflux.reader.result

@Suppress("unused")
sealed class JsResult<out T> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R> = when (this) {
        is Success -> Success(transform(value), path)
        is Failure -> this
    }

    fun <R> flatMap(transform: (T, path: JsResultPath) -> JsResult<R>): JsResult<R> = when (this) {
        is Success -> transform(value, this.path)
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

    class Success<T>(val value: T, val path: JsResultPath) : JsResult<T>()

    class Failure(val errors: List<Pair<JsResultPath, List<JsError>>>) : JsResult<Nothing>() {

        constructor(path: JsResultPath, error: JsError) : this(path, listOf(error))

        constructor(path: JsResultPath, errors: List<JsError>) : this(listOf(Pair(path, errors)))
    }
}

fun <T> T.asSuccess(path: JsResultPath): JsResult<T> =
    JsResult.Success(value = this, path = path)

fun <E : JsError> E.asFailure(path: JsResultPath): JsResult<Nothing> =
    JsResult.Failure(path = path, error = this)

fun <E : JsError> List<E>.asFailure(path: JsResultPath): JsResult<Nothing> =
    JsResult.Failure(path = path, errors = this)
