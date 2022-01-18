package io.github.airflux.core.reader.result

@Suppress("unused")
sealed class JsResult<out T> {

    companion object;

    infix fun <R> map(transform: (T) -> R): JsResult<R> = when (this) {
        is Success -> Success(transform(value), location)
        is Failure -> this
    }

    fun <R> flatMap(transform: (T, location: JsLocation) -> JsResult<R>): JsResult<R> = when (this) {
        is Success -> transform(value, this.location)
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

    class Success<T>(val value: T, val location: JsLocation) : JsResult<T>()

    class Failure private constructor(val causes: List<Cause>) : JsResult<Nothing>() {

        constructor(location: JsLocation, error: JsError) : this(listOf(Cause(location, JsErrors.of(error))))

        constructor(location: JsLocation, errors: JsErrors) : this(listOf(Cause(location, errors)))

        operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        data class Cause(val location: JsLocation, val errors: JsErrors) {

            companion object {
                infix fun JsLocation.bind(error: JsError): Cause = Cause(location = this, errors = JsErrors.of(error))

                infix fun Int.bind(error: JsError): Cause =
                    Cause(location = JsLocation.empty.append(this), errors = JsErrors.of(error))

                infix fun String.bind(error: JsError): Cause =
                    Cause(location = JsLocation.empty.append(this), errors = JsErrors.of(error))
            }
        }

        companion object {
            fun Collection<Failure>.merge(): Failure = Failure(causes = flatMap { failure -> failure.causes })
        }
    }
}

fun <T> T.asSuccess(location: JsLocation): JsResult<T> =
    JsResult.Success(value = this, location = location)

fun <E : JsError> E.asFailure(location: JsLocation): JsResult<Nothing> =
    JsResult.Failure(location = location, error = this)
