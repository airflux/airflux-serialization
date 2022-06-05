package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.`object`.validator.and
import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalProperties
import io.github.airflux.dsl.reader.`object`.validator.base.IsNotEmpty
import io.github.airflux.dsl.reader.`object`.validator.base.MaxProperties
import io.github.airflux.dsl.reader.scope.JsArrayReaderConfiguration
import io.github.airflux.dsl.reader.scope.objectReaderConfiguration

val ObjectReaderConfiguration = objectReaderConfiguration {

    checkUniquePropertyPath = true

    validation {
        before = AdditionalProperties
        after = IsNotEmpty and MaxProperties(10)
    }
}

val ArrayReaderConfiguration = JsArrayReaderConfiguration.DEFAULT
