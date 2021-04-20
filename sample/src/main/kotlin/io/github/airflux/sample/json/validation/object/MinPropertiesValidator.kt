package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.JsProperty
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValidator
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.reader.result.JsError
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidations.Builder.minProperties: Int
    get() = after[MinPropertiesValidator.NAME]
        ?.let { (it as MinPropertiesValidator.Builder).value }
        ?: -1
    set(value) {
        if (value > 0)
            after.add(MinPropertiesValidator.Builder(value))
        else
            after.remove(MinPropertiesValidator.NAME)
    }

class MinPropertiesValidator private constructor(val value: Int) : ObjectValidator.After {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        properties: List<JsProperty<*>>,
        objectValuesMap: ObjectValuesMap
    ): List<JsError> =
        if (objectValuesMap.size < value)
            listOf(JsonErrors.Validation.Object.MinProperties(expected = value, actual = objectValuesMap.size))
        else
            emptyList()

    class Builder(val value: Int) : ObjectValidator.After.Builder {

        override val key: String
            get() = MaxPropertiesValidator.NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            properties: List<JsProperty<*>>
        ): ObjectValidator.After = MinPropertiesValidator(value)
    }

    companion object {
        const val NAME: String = "MinPropertiesValidator"
    }
}
