package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.serialization.dsl.reader.context.ReaderContextBuilder
import io.github.airflux.serialization.dsl.reader.context.exception.exception
import io.github.airflux.serialization.dsl.reader.context.exception.exceptionsHandler
import io.github.airflux.serialization.dsl.reader.context.readerContext
import io.github.airflux.serialization.std.validator.array.IsNotEmptyArrayValidator
import io.github.airflux.serialization.std.validator.array.MaxItemsArrayValidator
import io.github.airflux.serialization.std.validator.array.MinItemsArrayValidator
import io.github.airflux.serialization.std.validator.comparison.EqComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.GeComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.GtComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.LeComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.LtComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.MaxComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.MinComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.NeComparisonValidator
import io.github.airflux.serialization.std.validator.string.IsAStringValidator
import io.github.airflux.serialization.std.validator.string.IsNotBlankStringValidator
import io.github.airflux.serialization.std.validator.string.MaxLengthStringValidator
import io.github.airflux.serialization.std.validator.string.MinLengthStringValidator
import io.github.airflux.serialization.std.validator.string.PatternStringValidator
import io.github.airflux.serialization.std.validator.struct.AdditionalPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.struct.IsNotEmptyObjectValidator
import io.github.airflux.serialization.std.validator.struct.MaxPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.struct.MinPropertiesObjectValidator

val DefaultReaderContext = readerContext {
    failFast = false

    registerExceptionHandler()
    registerErrorBuilders()
}

fun ReaderContextBuilder.registerExceptionHandler() {
    +exceptionsHandler(
        exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing },
        exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
    )
}

fun ReaderContextBuilder.registerErrorBuilders() {
    readerErrorBuilders()
    objectValidationErrorBuilders()
    arrayValidationErrorBuilders()
    stringValidationErrorBuilders()
    comparableValidationErrorBuilders()
}

fun ReaderContextBuilder.readerErrorBuilders() {
    +PathMissingErrorBuilder { JsonErrors.PathMissing }
    +InvalidTypeErrorBuilder(JsonErrors::InvalidType)
    +ValueCastErrorBuilder(JsonErrors::ValueCast)
    +AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems }
}

fun ReaderContextBuilder.objectValidationErrorBuilders() {
    +AdditionalPropertiesObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.AdditionalProperties }
    +IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.IsEmpty }
    +MinPropertiesObjectValidator.ErrorBuilder(JsonErrors.Validation.Object::MinProperties)
    +MaxPropertiesObjectValidator.ErrorBuilder(JsonErrors.Validation.Object::MaxProperties)
}

fun ReaderContextBuilder.arrayValidationErrorBuilders() {
    +IsNotEmptyArrayValidator.ErrorBuilder { JsonErrors.Validation.Arrays.IsEmpty }
    +MinItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MinItems)
    +MaxItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MaxItems)
}

fun ReaderContextBuilder.stringValidationErrorBuilders() {
    +IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
    +IsNotBlankStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsBlank }
    +MinLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MinLength)
    +MaxLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MaxLength)
    +PatternStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::Pattern)
    +IsAStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::IsA)
}

fun ReaderContextBuilder.comparableValidationErrorBuilders() {
    +MinComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Min)
    +MaxComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Max)
    +EqComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Eq)
    +NeComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Ne)
    +GtComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Gt)
    +GeComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Ge)
    +LtComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Lt)
    +LeComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Le)
}
