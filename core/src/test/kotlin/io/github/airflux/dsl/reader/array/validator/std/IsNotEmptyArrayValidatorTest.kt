package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.github.airflux.common.JsonErrors
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsNotEmptyArrayValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
    }

    init {

        "The array validator IsNotEmpty" - {
            val validator = ArrayValidator.isNotEmpty.build()

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val input: JsArray<JsString> = JsArray()

                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, input)
                    }
                    exception.message shouldBe "Key '${IsNotEmptyArrayValidator.ErrorBuilder.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsNotEmptyArrayValidator.ErrorBuilder { JsonErrors.Validation.Arrays.IsEmpty }
                )

                "when an array is empty" - {
                    val input: JsArray<JsString> = JsArray()

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, input)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.IsEmpty
                        )
                    }
                }

                "when an array is not empty" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"))

                    "then the validator should do not return any errors" {
                        val failure = validator.validation(context, LOCATION, input)
                        failure.shouldBeNull()
                    }
                }
            }
        }
    }
}
