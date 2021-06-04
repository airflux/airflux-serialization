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
        operator fun ObjectValidator.Identifier.unaryMinus() {
            if (!before.remove(this.id)) after.remove(this.id)
        }

        internal fun build(): ObjectValidators = ObjectValidators(before, after)
    }

    internal class Validators<T>(other: Validators<T>? = null) : Iterable<T>
        where T : ObjectValidator.Builder<*> {

        private val items: MutableMap<ObjectValidator.Id<*>, T> =
            if (other != null) LinkedHashMap(other.items) else LinkedHashMap()

        operator fun contains(id: ObjectValidator.Id<*>): Boolean = items.containsKey(id)

        operator fun contains(validator: T): Boolean = items.containsKey(validator.id)

        operator fun get(id: ObjectValidator.Id<*>): T? = items[id]

        fun add(validator: T) {
            items[validator.id] = validator
        }

        fun remove(id: ObjectValidator.Id<*>): Boolean = items.remove(id) != null

        override fun iterator(): Iterator<T> = items.values.iterator()
    }

    companion object {
        val Default = ObjectValidators(before = Validators(), after = Validators())

        fun build(init: Builder.() -> Unit): ObjectValidators = Builder().apply(init).build()
    }
}
