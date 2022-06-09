package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.dsl.common.JsonErrors
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.additionalProperties
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class AdditionalPropertiesTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-name"
        private const val NAME_PROPERTY_NAME = "title"
        private const val NAME_PROPERTY_VALUE = "property-title"
        private val LOCATION = JsLocation.empty

        private val idProperty: JsObjectProperty.Required<String> =
            JsObjectProperty.Required(required(ID_PROPERTY_NAME, StringReader))

        val properties: JsObjectProperties = JsObjectProperties.Builder()
            .apply { add(idProperty) }
            .build(false)
    }

    init {

        "The object validator AdditionalProperties" - {
            val validator: JsObjectValidator.Before = additionalProperties.build(properties)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()
                val input = JsObject(
                    ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                    TITLE_PROPERTY_VALUE to JsString(TITLE_PROPERTY_NAME)
                )

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, properties, input)
                    }
                    exception.message shouldBe "Key '${AdditionalProperties.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    AdditionalProperties.ErrorBuilder { JsonErrors.Validation.Object.AdditionalProperties }
                )

                "when the object is empty" - {
                    val input = JsObject()

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, properties, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object does not contains additional properties" - {
                    val input = JsObject(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, properties, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object contains additional properties" - {
                    val input = JsObject(
                        ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                        TITLE_PROPERTY_VALUE to JsString(TITLE_PROPERTY_NAME),
                        NAME_PROPERTY_VALUE to JsString(NAME_PROPERTY_NAME)
                    )

                    "when fail-fast is missing" - {

                        "then the validator should return an error" {
                            val failure = validator.validation(context, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Object.AdditionalProperties
                            )
                        }
                    }

                    "when fail-fast is true" - {
                        val contextWithFailFast = context + FailFast(true)

                        "then the validator should return an error" {
                            val failure = validator.validation(contextWithFailFast, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Object.AdditionalProperties
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val contextWithFailFast = context + FailFast(false)

                        "then the validator should return an error" {
                            val failure = validator.validation(contextWithFailFast, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                JsResult.Failure(
                                    location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                    error = JsonErrors.Validation.Object.AdditionalProperties
                                ),
                                JsResult.Failure(
                                    location = LOCATION.append(NAME_PROPERTY_VALUE),
                                    error = JsonErrors.Validation.Object.AdditionalProperties
                                )
                            ).merge()
                        }
                    }
                }
            }
        }
    }
}
