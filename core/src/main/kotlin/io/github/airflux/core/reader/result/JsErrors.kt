package io.github.airflux.core.reader.result

class JsErrors private constructor(private val items: List<JsError>) : Collection<JsError> by items {

    operator fun plus(other: JsErrors): JsErrors = JsErrors(items + other.items)

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsErrors && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    companion object {
        fun of(error: JsError, vararg errors: JsError): JsErrors = if (errors.isEmpty())
            JsErrors(listOf(error))
        else
            JsErrors(listOf(error) + errors.asList())

        fun of(errors: List<JsError>): JsErrors? = errors.takeIf { it.isNotEmpty() }?.let { JsErrors(it) }
    }
}
