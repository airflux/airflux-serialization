package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.validator.JsObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.AdditionalPropertiesValidator
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

internal object AdditionalPropertiesValidatorBuilder : JsObjectValidatorBuilder.Before {

    override fun build(properties: JsObjectProperties): JsObjectValidator.Before {
        val names: Set<String> = properties.names()
        return AdditionalPropertiesValidator(names)
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
