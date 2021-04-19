package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsError

class ObjectValidations<E : JsError>(
    val before: List<ObjectValidator.Before<E>>,
    val after: List<ObjectValidator.After<E>>
) {

    @ObjectReaderMarker
    class Builder<E : JsError> private constructor(
        val before: Validators<ObjectValidator.Before.Builder<E>>,
        val after: Validators<ObjectValidator.After.Builder<E>>
    ) {

        constructor(other: Builder<E>? = null) : this(
            before = Validators<ObjectValidator.Before.Builder<E>>()
                .apply { other?.before?.forEach { add(it) } },
            after = Validators<ObjectValidator.After.Builder<E>>()
                .apply { other?.after?.forEach { add(it) } }
        )

        fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*, E>>): ObjectValidations<E> =
            ObjectValidations(
                before = before.map { validator -> validator.build(configuration, attributes) },
                after = after.map { validator -> validator.build(configuration, attributes) }
            )

        class Validators<T : ObjectValidator.Identifier> : Iterable<T> {

            private val items: MutableMap<String, T> = LinkedHashMap()

            operator fun contains(name: String): Boolean = items.containsKey(name)

            operator fun contains(validator: T): Boolean = items.containsKey(validator.key)

            operator fun get(name: String): T? = items[name]

            fun add(validator: T) {
                items[validator.key] = validator
            }

            fun remove(name: String) {
                items.remove(name)
            }

            override fun iterator(): Iterator<T> = items.values.iterator()
        }

//        companion object {
//            val Default = Builder<E>(before = Validators(), after = Validators())
//        }
    }
}
