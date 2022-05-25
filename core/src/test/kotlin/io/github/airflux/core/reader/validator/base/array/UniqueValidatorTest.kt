package io.github.airflux.core.reader.validator.base.array

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.base.BaseArrayValidators
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class UniqueValidatorTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
    }

    init {

        "The isUnique validator" - {
            val validator = BaseArrayValidators.isUnique(
                keySelector = { key: String -> key },
                error = { index, value ->
                    JsonErrors.Validation.Arrays.Unique(index = index, value = value)
                }
            )

            "when a collection is empty" - {
                val collection = emptyList<String>()

                "then the validator should do not return any errors" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldBeNull()
                }
            }

            "when a collection does not contain duplicate" - {
                val collection = listOf("A", "B")

                "then the validator should do not return any errors" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldBeNull()
                }
            }

            "when a collection contains duplicate" - {
                val collection = listOf("A", "B", "A", "B", "C")

                "and fail-fast is missing" - {

                    "then the validator should return the first error" {
                        val errors = validator.validation(context, location, collection)
                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Arrays.Unique(index = 2, value = "A")
                        )
                    }
                }

                "and fail-fast is true" - {
                    val failFastContext = context + FailFast(true)

                    "then the validator should return the first error" {
                        val errors = validator.validation(failFastContext, location, collection)
                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Arrays.Unique(index = 2, value = "A")
                        )
                    }
                }

                "and fail-fast is false" - {
                    val failFastContext = context + FailFast(false)

                    "then the validator should return all errors" {
                        val errors = validator.validation(failFastContext, location, collection)
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
