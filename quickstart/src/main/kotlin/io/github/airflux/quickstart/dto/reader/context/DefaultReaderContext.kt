package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.serialization.dsl.reader.context.JsReaderContextBuilder
import io.github.airflux.serialization.dsl.reader.context.readerContext
import io.github.airflux.serialization.std.validator.array.IsNotEmptyArrayValidator
import io.github.airflux.serialization.std.validator.array.MaxItemsArrayValidator
import io.github.airflux.serialization.std.validator.array.MinItemsArrayValidator
import io.github.airflux.serialization.std.validator.comparable.EqComparableValidator
import io.github.airflux.serialization.std.validator.comparable.GeComparableValidator
import io.github.airflux.serialization.std.validator.comparable.GtComparableValidator
import io.github.airflux.serialization.std.validator.comparable.LeComparableValidator
import io.github.airflux.serialization.std.validator.comparable.LtComparableValidator
import io.github.airflux.serialization.std.validator.comparable.MaxComparableValidator
import io.github.airflux.serialization.std.validator.comparable.MinComparableValidator
import io.github.airflux.serialization.std.validator.comparable.NeComparableValidator
import io.github.airflux.serialization.std.validator.`object`.AdditionalPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.`object`.IsNotEmptyObjectValidator
import io.github.airflux.serialization.std.validator.`object`.MaxPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.`object`.MinPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.string.IsAStringValidator
import io.github.airflux.serialization.std.validator.string.IsNotBlankStringValidator
import io.github.airflux.serialization.std.validator.string.MaxLengthStringValidator
import io.github.airflux.serialization.std.validator.string.MinLengthStringValidator
import io.github.airflux.serialization.std.validator.string.PatternStringValidator

val DefaultReaderContext = readerContext {
    failFast = false

    errorBuilders {
        readerErrorBuilders()
        objectValidationErrorBuilders()
        arrayValidationErrorBuilders()
        stringValidationErrorBuilders()
        comparableValidationErrorBuilders()
    }

    exceptions {
        exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
        exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
    }
}

fun JsReaderContextBuilder.ErrorsBuilder.readerErrorBuilders() {
    register(PathMissingErrorBuilder { JsonErrors.PathMissing })
    +InvalidTypeErrorBuilder(JsonErrors::InvalidType)
    +ValueCastErrorBuilder(JsonErrors::ValueCast)
    +AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems }
}

fun JsReaderContextBuilder.ErrorsBuilder.objectValidationErrorBuilders() {
    +AdditionalPropertiesObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.AdditionalProperties }
    +IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.IsEmpty }
    +MinPropertiesObjectValidator.ErrorBuilder(JsonErrors.Validation.Object::MinProperties)
    +MaxPropertiesObjectValidator.ErrorBuilder(JsonErrors.Validation.Object::MaxProperties)
}

fun JsReaderContextBuilder.ErrorsBuilder.arrayValidationErrorBuilders() {
    +IsNotEmptyArrayValidator.ErrorBuilder { JsonErrors.Validation.Arrays.IsEmpty }
    +MinItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MinItems)
    +MaxItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MaxItems)
}

fun JsReaderContextBuilder.ErrorsBuilder.stringValidationErrorBuilders() {
    +IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
    +IsNotBlankStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsBlank }
    +MinLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MinLength)
    +MaxLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MaxLength)
    +PatternStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::Pattern)
    +IsAStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::IsA)
}

fun JsReaderContextBuilder.ErrorsBuilder.comparableValidationErrorBuilders() {
    +MinComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Min)
    +MaxComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Max)
    +EqComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Eq)
    +NeComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Ne)
    +GtComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Gt)
    +GeComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Ge)
    +LtComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Lt)
    +LeComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Le)
}
