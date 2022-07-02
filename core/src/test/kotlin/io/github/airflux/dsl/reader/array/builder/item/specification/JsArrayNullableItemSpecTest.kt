package io.github.airflux.dsl.reader.array.builder.item.specification

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.github.airflux.std.reader.IntReader
import io.github.airflux.std.reader.StringReader
import io.github.airflux.std.validator.condition.applyIfNotNull
import io.github.airflux.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.std.validator.string.StringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayNullableItemSpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val CONTEXT =
            JsReaderContext(
                listOf(
                    IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty },
                    PathMissingErrorBuilder { JsonErrors.PathMissing },
                    InvalidTypeErrorBuilder(JsonErrors::InvalidType)
                )
            )
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsArrayItemSpec#Nullable" - {

            "when creating the instance of the spec" - {
                val spec = nullable(reader = StringReader)

                "when the reader has read an item" - {

                    "when the reader has successfully read" - {

                        "if the attribute value is not the null type" - {
                            val input = JsString(ID_VALUE_AS_UUID)
                            val result = spec.reader.read(CONTEXT, LOCATION, input)

                            "then the not-null value should be returned" {
                                result as JsResult.Success<String?>
                                result.value shouldBe ID_VALUE_AS_UUID
                            }
                        }

                        "if the attribute value is the null type" - {
                            val input = JsNull
                            val result = spec.reader.read(CONTEXT, LOCATION, input)

                            "then the null value should be returned" {
                                result as JsResult.Success<String?>
                                result.value.shouldBeNull()
                            }
                        }
                    }

                    "when a read error occurred" - {
                        val input = JsNumber.valueOf(10)
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then should be returned a read error" {
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.NUMBER
                                    )
                                )
                            )
                        }
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = JsArrayItemSpec.Nullable(reader = StringReader)
                val specWithValidator = spec.validation(StringValidator.isNotEmpty.applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val input = JsString(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val input = JsString("")

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val input = JsNumber.valueOf(10)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = nullable(reader = StringReader)
                val alt = nullable(reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "when the main reader has successfully read" - {
                    val input = JsString(ID_VALUE_AS_UUID)
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned" {
                        println(result)
                        result as JsResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }
                }

                "when the main reader has failure read" - {
                    val input = JsNumber.valueOf(ID_VALUE_AS_INT)!!
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned from the alternative reader" {
                        result as JsResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_INT
                    }
                }

                "when the alternative reader has failure read" - {
                    val input = JsBoolean.True
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned all read errors" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.BOOLEAN
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.BOOLEAN
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
