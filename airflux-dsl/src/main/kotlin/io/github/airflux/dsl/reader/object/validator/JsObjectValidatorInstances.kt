package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

internal class JsObjectValidatorInstances private constructor(
    val before: List<JsObjectValidator.Before>,
    val after: List<JsObjectValidator.After>
) {
    companion object {

        fun of(
            validators: JsObjectValidators,
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): JsObjectValidatorInstances {
            val before = validators.before
                .map { validator -> validator.build(configuration, properties) }
            val after = validators.after
                .map { validator -> validator.build(configuration, properties) }
            return if (before.isEmpty() && after.isEmpty()) Empty else JsObjectValidatorInstances(before, after)
        }

        private val Empty = JsObjectValidatorInstances(emptyList(), emptyList())
    }
}
