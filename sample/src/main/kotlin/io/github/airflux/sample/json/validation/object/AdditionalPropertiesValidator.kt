package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.Attribute
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValidator
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.result.JsError
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidations.Builder.additionalProperties: Boolean
    get() = AdditionalPropertiesValidator.NAME in before
    set(value) {
        if (value)
            before.add(AdditionalPropertiesValidator.Builder())
        else
            before.remove(AdditionalPropertiesValidator.NAME)
    }

class AdditionalPropertiesValidator private constructor(private val names: Set<String>) : ObjectValidator.Before {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        attributes: List<Attribute<*>>
    ): List<JsError> {
        val unknownProperties = mutableListOf<String>()
        input.underlying
            .forEach { (name, _) ->
                if (name !in names) {
                    unknownProperties.add(name)
                    if (configuration.failFast) return@forEach
                }
            }

        return if (unknownProperties.isNotEmpty())
            listOf(JsonErrors.Validation.Object.AdditionalProperties(unknownProperties))
        else
            emptyList()
    }

    class Builder : ObjectValidator.Before.Builder {

        override val key: String
            get() = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            attributes: List<Attribute<*>>
        ): ObjectValidator.Before =
            mutableSetOf<String>()
                .apply {
                    attributes.forEach { attribute ->
                        attribute.name
                            .value
                            .elements
                            .firstOrNull()
                            ?.let {
                                when (it) {
                                    is KeyPathElement -> add(it.key)
                                    is IdxPathElement -> Unit
                                }
                            }
                    }
                }
                .let { AdditionalPropertiesValidator(it) }
    }

    companion object {
        const val NAME: String = "additionalProperties"
    }
}
