package io.github.airflux.core.reader.validator.std.array

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.core.reader.result.JsLocation
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsUniqueArrayValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
    }

    init {

        "The array validator IsUnique" - {
            val validator = ArrayValidator.isUnique(keySelector = { key: String -> key })

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, listOf("A", "A"))
                    }
                    exception.message shouldBe "Key '${IsUniqueArrayValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsUniqueArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::Unique)
                )

                "when a collection is empty" - {
                    val collection = emptyList<String>()

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldBeNull()
                    }
                }

                "when a collection does not contain duplicate" - {
                    val collection = listOf("A", "B")

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldBeNull()
                    }
                }

                "when a collection contains duplicate" - {
                    val collection = listOf("A", "B", "A", "B", "C")

                    "and fail-fast is missing" - {

                        "then the validator should return the first error" {
                            val errors = validator.validation(context, LOCATION, collection)
                            errors.shouldNotBeNull()
                            errors.items shouldContainExactly listOf(
                                JsonErrors.Validation.Arrays.Unique(index = 2, value = "A")
                            )
                        }
                    }

                    "and fail-fast is true" - {
                        val failFastContext = context + FailFast(true)

                        "then the validator should return the first error" {
                            val errors = validator.validation(failFastContext, LOCATION, collection)
                            errors.shouldNotBeNull()
                            errors.items shouldContainExactly listOf(
                                JsonErrors.Validation.Arrays.Unique(index = 2, value = "A")
                            )
                        }
                    }

                    "and fail-fast is false" - {
                        val failFastContext = context + FailFast(false)

                        "then the validator should return all errors" {
                            val errors = validator.validation(failFastContext, LOCATION, collection)
                            errors.shouldNotBeNull()
                            errors.items shouldContainExactly listOf(
                                JsonErrors.Validation.Arrays.Unique(index = 2, value = "A"),
                                JsonErrors.Validation.Arrays.Unique(index = 3, value = "B")
                            )
                        }
                    }
                }
            }
        }
    }
}
