package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.AirfluxMarker

class JsObjectValidators private constructor(
    internal val before: Validators<JsObjectValidator.Before.Builder>,
    internal val after: Validators<JsObjectValidator.After.Builder>
) {

    @AirfluxMarker
    class Builder internal constructor(other: JsObjectValidators? = null) {
        private val before = Validators(other?.before)
        private val after = Validators(other?.after)

        operator fun JsObjectValidator.Before.Builder.unaryPlus() = before.add(this)
        operator fun JsObjectValidator.After.Builder.unaryPlus() = after.add(this)
        operator fun JsObjectValidator.Identifier.unaryMinus() {
            if (!before.remove(this.id)) after.remove(this.id)
        }

        internal fun build(): JsObjectValidators = JsObjectValidators(before, after)
    }

    internal class Validators<T>(other: Validators<T>? = null) : Iterable<T>
        where T : JsObjectValidator.Builder<*> {

        private val items: MutableMap<JsObjectValidator.Id<*>, T> =
            if (other != null) LinkedHashMap(other.items) else LinkedHashMap()

        operator fun contains(id: JsObjectValidator.Id<*>): Boolean = items.containsKey(id)

        operator fun contains(validator: T): Boolean = items.containsKey(validator.id)

        operator fun get(id: JsObjectValidator.Id<*>): T? = items[id]

        fun add(validator: T) {
            items[validator.id] = validator
        }

        fun remove(id: JsObjectValidator.Id<*>): Boolean = items.remove(id) != null

        override fun iterator(): Iterator<T> = items.values.iterator()
    }

    companion object {
        val Default = JsObjectValidators(before = Validators(), after = Validators())

        fun build(init: Builder.() -> Unit): JsObjectValidators = Builder().apply(init).build()
    }
}
