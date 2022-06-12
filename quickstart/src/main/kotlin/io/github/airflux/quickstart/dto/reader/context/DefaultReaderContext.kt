package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.validator.std.array.IsNotEmptyArrayValidator
import io.github.airflux.core.reader.validator.std.comparable.EqComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.GeComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.GtComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.LeComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.LtComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.MaxComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.MinComparableValidator
import io.github.airflux.core.reader.validator.std.comparable.NeComparableValidator
import io.github.airflux.core.reader.validator.std.string.IsAStringValidator
import io.github.airflux.core.reader.validator.std.string.IsNotBlankStringValidator
import io.github.airflux.core.reader.validator.std.string.IsNotEmptyStringValidator
import io.github.airflux.core.reader.validator.std.string.MaxLengthStringValidator
import io.github.airflux.core.reader.validator.std.string.MinLengthStringValidator
import io.github.airflux.core.reader.validator.std.string.PatternStringValidator
import io.github.airflux.core.reader.validator.std.array.IsUniqueArrayValidator
import io.github.airflux.core.reader.validator.std.array.MaxItemsArrayValidator
import io.github.airflux.core.reader.validator.std.array.MinItemsArrayValidator
import io.github.airflux.dsl.reader.context.JsReaderContextBuilder
import io.github.airflux.dsl.reader.context.readerContext
import io.github.airflux.core.reader.validator.std.`object`.AdditionalPropertiesObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.IsNotEmptyObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.MaxPropertiesObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.MinPropertiesObjectValidator
import io.github.airflux.quickstart.json.error.JsonErrors

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
    +IsUniqueArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::Unique)
    +IsNotEmptyArrayValidator.ErrorBuilder { JsonErrors.Validation.Arrays.IsEmpty }
    +MinItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MinItems)
    +MaxItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MaxItems)
}

fun JsReaderContextBuilder.ErrorsBuilder.stringValidationErrorBuilders() {
    +IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
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
