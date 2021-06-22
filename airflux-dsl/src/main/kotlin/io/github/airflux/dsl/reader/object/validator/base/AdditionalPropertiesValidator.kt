package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

class AdditionalProperties(private val errorBuilder: ErrorBuilder) :
    JsObjectValidator.Identifier,
    JsObjectValidator.Before.Builder {

    override val id = AdditionalProperties

    override fun build(
        configuration: ObjectReaderConfiguration,
        properties: List<JsReaderProperty<*>>
    ): JsObjectValidator.Before = mutableSetOf<String>()
        .apply {
            properties.forEach { property ->
                property.propertyPath
                    .firstOrNull()
                    ?.let {
                        when (it) {
                            is KeyPathElement -> add(it.key)
                            is IdxPathElement -> Unit
                        }
                    }
            }
        }
        .let { Validator(it, errorBuilder) }

    private class Validator(val names: Set<String>, val errorBuilder: ErrorBuilder) : JsObjectValidator.Before {

        override fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            context: JsReaderContext?
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
                listOf(errorBuilder.build(unknownProperties))
            else
                emptyList()
        }
    }

    fun interface ErrorBuilder {
        fun build(properties: List<String>): JsError
    }

    companion object Id : JsObjectValidator.Id<Validator>
}
