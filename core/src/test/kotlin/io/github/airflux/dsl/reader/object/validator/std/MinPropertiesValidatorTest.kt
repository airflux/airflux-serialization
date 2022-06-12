package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.common.JsonErrors
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.ObjectValuesMapInstance
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.minProperties
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinPropertiesValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "property-name"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-title"
        private const val MIN_PROPERTIES = 2
        private val LOCATION = JsLocation.empty

        private val input = JsObject()
        private val idProperty: JsObjectProperty.Required<String> =
            JsObjectProperty.Required(required(ID_PROPERTY_NAME, StringReader))
        private val nameProperty: JsObjectProperty.Required<String> =
            JsObjectProperty.Required(required(NAME_PROPERTY_NAME, StringReader))
        private val titleProperty: JsObjectProperty.Required<String> =
            JsObjectProperty.Required(required(TITLE_PROPERTY_NAME, StringReader))
        val properties: JsObjectProperties = JsObjectProperties.Builder()
            .apply {
                add(idProperty)
                add(nameProperty)
                add(titleProperty)
            }
            .build(false)
    }

    init {

        "The object validator MinProperties" - {
            val validator: JsObjectValidator.After = minProperties(MIN_PROPERTIES).build(properties)

            "when the reader context does not contain the error builder" - {
                val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, properties, objectValuesMap, input)
                    }
                    exception.message shouldBe "Key '${MinPropertiesValidator.ErrorBuilder.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MinPropertiesValidator.ErrorBuilder(JsonErrors.Validation.Object::MinProperties)
                )

                "when the object is empty" - {
                    val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, properties, objectValuesMap, input)
                        errors.shouldNotBeNull()
                        errors shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Object.MinProperties(expected = MIN_PROPERTIES, actual = 0)
                        )
                    }
                }

                "when the object contains a number of properties less than the minimum" - {
                    val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[idProperty] = ID_PROPERTY_VALUE
                    }

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, properties, objectValuesMap, input)
                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Object.MinProperties(expected = MIN_PROPERTIES, actual = 1)
                        )
                    }
                }

                "when the object contains a number of properties equal to the minimum" - {
                    val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[idProperty] = ID_PROPERTY_VALUE
                        this[nameProperty] = NAME_PROPERTY_VALUE
                    }

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, properties, objectValuesMap, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object contains a number of properties more than the minimum" - {
                    val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance().apply {
                        this[idProperty] = ID_PROPERTY_VALUE
                        this[nameProperty] = NAME_PROPERTY_VALUE
                        this[titleProperty] = TITLE_PROPERTY_VALUE
                    }

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, properties, objectValuesMap, input)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}