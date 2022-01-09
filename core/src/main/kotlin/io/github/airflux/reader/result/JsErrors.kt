package io.github.airflux.reader.result

class JsErrors private constructor(private val items: List<JsError>) : Iterable<JsError> by items {

    operator fun plus(other: JsErrors): JsErrors = JsErrors(items + other.items)

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsErrors && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    companion object {
        fun of(error: JsError): JsErrors = JsErrors(listOf(error))

        fun of(errors: List<JsError>): JsErrors? = errors.takeIf { it.isNotEmpty() }?.let { JsErrors(it) }
    }
}
