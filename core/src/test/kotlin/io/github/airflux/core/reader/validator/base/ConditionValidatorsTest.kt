package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsPropertyValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull

class ConditionValidatorsTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
        private val isNotEmpty: JsPropertyValidator<String> =
            JsPropertyValidator { _, _, value ->
                if (value.isNotEmpty()) null else JsErrors.of(JsonErrors.Validation.Strings.IsEmpty)
            }
    }

    init {

        "JsPropertyValidator<T>#applyIfNotNull()" - {
            val validator = isNotEmpty.applyIfNotNull()

            "should return the result of applying the validator to the value if it is not the null value" {
                val errors = validator.validation(context, location, "")

                errors.shouldContainExactly(JsonErrors.Validation.Strings.IsEmpty)
            }

            "should return the null value if the value is the null value" {
                val errors = validator.validation(context, location, null)

                errors.shouldBeNull()
            }
        }

        "JsPropertyValidator<T>#applyIf(_)" - {

            "should return the result of applying the validator to the value if the predicate returns true" {
                val validator = isNotEmpty.applyIf { _, _, _ -> true }

                val errors = validator.validation(context, location, "")

                errors.shouldContainExactly(JsonErrors.Validation.Strings.IsEmpty)
            }

            "should return the null value if the predicate returns false" {
                val validator = isNotEmpty.applyIf { _, _, _ -> false }

                val errors = validator.validation(context, location, "")

                errors.shouldBeNull()
            }
        }
    }
}
