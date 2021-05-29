package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

internal class ObjectValidatorInstances private constructor(
    val before: List<ObjectValidator.Before>,
    val after: List<ObjectValidator.After>
) {
    companion object {

        fun of(
            validators: ObjectValidators,
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): ObjectValidatorInstances {
            val before = validators.before
                .map { validator -> validator.build(configuration, properties) }
            val after = validators.after
                .map { validator -> validator.build(configuration, properties) }
            return if (before.isEmpty() && after.isEmpty()) Empty else ObjectValidatorInstances(before, after)
        }

        private val Empty = ObjectValidatorInstances(emptyList(), emptyList())
    }
}
