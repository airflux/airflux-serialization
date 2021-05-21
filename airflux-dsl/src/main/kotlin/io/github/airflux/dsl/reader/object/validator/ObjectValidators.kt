package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidatorInstances
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

class ObjectValidators private constructor(
    val before: Validators<ObjectValidator.Before.Builder>,
    val after: Validators<ObjectValidator.After.Builder>
) {

    internal constructor(other: ObjectValidators) : this(
        before = Validators<ObjectValidator.Before.Builder>(other.before),
        after = Validators<ObjectValidator.After.Builder>(other.after)
    )

    internal fun build(
        configuration: ObjectReaderConfiguration,
        properties: List<JsReaderProperty<*>>
    ): ObjectValidatorInstances {
        val before = before.map { validator -> validator.build(configuration, properties) }
        val after = after.map { validator -> validator.build(configuration, properties) }
        return if (before.isEmpty() && after.isEmpty()) Empty else ObjectValidatorInstances(before, after)
    }

    @AirfluxMarker
    class Builder internal constructor() {
        val before = Validators<ObjectValidator.Before.Builder>()
        val after = Validators<ObjectValidator.After.Builder>()

        internal fun build(): ObjectValidators = ObjectValidators(before, after)
    }

    class Validators<T : ObjectValidator.Identifier> internal constructor(other: Validators<T>? = null) : Iterable<T> {

        private val items: MutableMap<String, T> = if (other != null) LinkedHashMap(other.items) else LinkedHashMap()

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

    companion object {
        val Default = ObjectValidators(before = Validators(), after = Validators())
        private val Empty = ObjectValidatorInstances(emptyList(), emptyList())

        fun build(init: Builder.() -> Unit): ObjectValidators = Builder().apply(init).build()
    }
}
