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

    class Failure private constructor(val causes: List<Cause>) : JsResult<Nothing>() {

        constructor(path: JsResultPath, error: JsError) : this(listOf(Cause(path, JsErrors.of(error))))

        constructor(path: JsResultPath, errors: JsErrors) : this(listOf(Cause(path, errors)))

        operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        data class Cause(val path: JsResultPath, val errors: JsErrors) {

            companion object {
                infix fun JsResultPath.bind(error: JsError): Cause = Cause(path = this, errors = JsErrors.of(error))

                infix fun Int.bind(error: JsError): Cause =
                    Cause(path = JsResultPath.Root / this, errors = JsErrors.of(error))

                infix fun String.bind(error: JsError): Cause =
                    Cause(path = JsResultPath.Root / this, errors = JsErrors.of(error))
            }
        }

        companion object {
            fun Collection<Failure>.merge(): Failure = Failure(causes = flatMap { failure -> failure.causes })
        }
    }
}

fun <T> T.asSuccess(path: JsResultPath): JsResult<T> =
    JsResult.Success(value = this, path = path)

fun <E : JsError> E.asFailure(path: JsResultPath): JsResult<Nothing> =
    JsResult.Failure(path = path, error = this)
