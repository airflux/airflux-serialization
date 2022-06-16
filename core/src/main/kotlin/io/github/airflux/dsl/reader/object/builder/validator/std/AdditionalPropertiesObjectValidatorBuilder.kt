package io.github.airflux.dsl.reader.`object`.builder.validator.std

import io.github.airflux.core.path.PathElement
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsObjectValidator
import io.github.airflux.std.validator.`object`.AdditionalPropertiesObjectValidator

internal object AdditionalPropertiesObjectValidatorBuilder : JsObjectValidatorBuilder.Before {

    override fun build(properties: JsObjectProperties): JsObjectValidator.Before {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesObjectValidator(names)
    }

    internal fun JsObjectProperties.names(): Set<String> {
        fun JsObjectProperty.names(): List<String> = path.items
            .mapNotNull { path ->
                when (val element = path.elements.first()) {
                    is PathElement.Key -> element.get
                    is PathElement.Idx -> null
                }
            }

        return flatMap { property -> property.names() }.toSet()
    }
}
