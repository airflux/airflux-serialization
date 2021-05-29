package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.AirfluxMarker

class ObjectValidators private constructor(
    internal val before: Validators<ObjectValidator.Before.Builder>,
    internal val after: Validators<ObjectValidator.After.Builder>
) {

    @AirfluxMarker
    class Builder internal constructor(other: ObjectValidators? = null) {
        private val before = Validators(other?.before)
        private val after = Validators(other?.after)

        operator fun ObjectValidator.Before.Builder.unaryPlus() = before.add(this)
        operator fun ObjectValidator.After.Builder.unaryPlus() = after.add(this)
        operator fun ObjectValidator.Before.Builder.unaryMinus() = before.remove(this.key)
        operator fun ObjectValidator.After.Builder.unaryMinus() = after.remove(this.key)

        internal fun build(): ObjectValidators = ObjectValidators(before, after)
    }

    internal class Validators<T : ObjectValidator.Identifier>(other: Validators<T>? = null) : Iterable<T> {

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

        fun build(init: Builder.() -> Unit): ObjectValidators = Builder().apply(init).build()
    }
}
