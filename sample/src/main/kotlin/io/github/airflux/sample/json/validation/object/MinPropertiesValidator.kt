package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.Attribute
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValidator
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidations.Builder<JsonErrors>.minProperties: Int
    get() = after[MinPropertiesValidator.NAME]
        ?.let { (it as MinPropertiesValidator.Builder).value }
        ?: -1
    set(value) {
        if (value > 0)
            after.add(MinPropertiesValidator.Builder(value))
        else
            after.remove(MinPropertiesValidator.NAME)
    }

class MinPropertiesValidator private constructor(val value: Int) : ObjectValidator.After<JsonErrors> {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        attributes: List<Attribute<*, JsonErrors>>,
        objectValuesMap: ObjectValuesMap<JsonErrors>
    ): List<JsonErrors> =
        if (objectValuesMap.size < value)
            listOf(JsonErrors.Validation.Object.MinProperties(expected = value, actual = objectValuesMap.size))
        else
            emptyList()

    class Builder(val value: Int) : ObjectValidator.After.Builder<JsonErrors> {

        override val key: String
            get() = MaxPropertiesValidator.NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            attributes: List<Attribute<*, JsonErrors>>
        ): ObjectValidator.After<JsonErrors> = MinPropertiesValidator(value)
    }

    companion object {
        const val NAME: String = "MinPropertiesValidator"
    }
}
