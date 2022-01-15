package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.core.path.IdxPathElement
import io.github.airflux.core.path.KeyPathElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.value.JsObject

@Suppress("unused")
class AdditionalPropertiesValidator private constructor(
    private val names: Set<String>,
    private val errorBuilder: ErrorBuilder
) : JsObjectValidator.Before {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        context: JsReaderContext,
        properties: List<JsReaderProperty>,
        input: JsObject
    ): JsErrors? {
        val unknownProperties = mutableListOf<String>()
        input.forEach { (name, _) ->
            if (name !in names) {
                unknownProperties.add(name)
                if (configuration.failFast) return@forEach
            }
        }
        return unknownProperties.takeIf { it.isNotEmpty() }
            ?.let { JsErrors.of(errorBuilder.build(it)) }
    }

    class Builder(private val errorBuilder: ErrorBuilder) {

        operator fun invoke(properties: List<JsReaderProperty>): JsObjectValidator.Before =
            AdditionalPropertiesValidator(properties.names(), errorBuilder)

        private fun List<JsReaderProperty>.names(): Set<String> =
            mapNotNull { property -> property.name() }
                .toSet()

        private fun JsReaderProperty.name(): String? = path.firstOrNull()
            ?.let {
                when (it) {
                    is KeyPathElement -> it.key
                    is IdxPathElement -> null
                }
            }
    }

    fun interface ErrorBuilder {
        fun build(properties: List<String>): JsError
    }
}
