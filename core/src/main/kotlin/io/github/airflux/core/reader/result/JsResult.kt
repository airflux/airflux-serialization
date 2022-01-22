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

    inline infix fun recovery(function: (Failure) -> JsResult<@UnsafeVariance T>): JsResult<T> = when (this) {
        is Success -> this
        is Failure -> function(this)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    data class Success<T>(val value: T, val location: JsLocation) : JsResult<T>()

    class Failure private constructor(val causes: List<Cause>) : JsResult<Nothing>() {

        constructor(location: JsLocation, error: JsError) : this(listOf(Cause(location, error)))

        constructor(location: JsLocation, errors: JsErrors) : this(listOf(Cause(location, errors)))

        operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        class Cause(val location: JsLocation, val errors: JsErrors) {

            constructor(location: JsLocation, error: JsError) : this(location, JsErrors.of(error))

            override fun equals(other: Any?): Boolean =
                this === other || (other is Cause && this.location == other.location && this.errors == other.errors)

            override fun hashCode(): Int {
                var result = location.hashCode()
                result = 31 * result + errors.hashCode()
                return result
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
