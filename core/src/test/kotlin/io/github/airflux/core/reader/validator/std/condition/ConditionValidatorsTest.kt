package io.github.airflux.core.reader.validator.std.condition

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.base.applyIf
import io.github.airflux.core.reader.validator.base.applyIfNotNull
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class ConditionValidatorsTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
        private val isNotEmpty: JsValidator<String> =
            JsValidator { _, _, value ->
                if (value.isNotEmpty()) null else JsErrors.of(JsonErrors.Validation.Strings.IsEmpty)
            }
    }

    init {

        "JsPropertyValidator<T>#applyIfNotNull()" - {
            val validator = isNotEmpty.applyIfNotNull()

            "should return the result of applying the validator to the value if it is not the null value" {
                val errors = validator.validation(context, location, "")

                errors.shouldNotBeNull()
                    .items.shouldContainExactly(JsonErrors.Validation.Strings.IsEmpty)
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

                errors.shouldNotBeNull()
                    .items.shouldContainExactly(JsonErrors.Validation.Strings.IsEmpty)
            }

            "should return the null value if the predicate returns false" {
                val validator = isNotEmpty.applyIf { _, _, _ -> false }

                val errors = validator.validation(context, location, "")

                errors.shouldBeNull()
            }
        }
    }
}
