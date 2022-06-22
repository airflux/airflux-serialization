package io.github.airflux.dsl.reader.config

import io.github.airflux.std.validator.array.ArrayValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class JsArrayReaderConfigTest : FreeSpec() {

    init {

        "when any validator is not registered in the configuration builder" - {
            val config: JsArrayReaderConfig = arrayReaderConfig({})

            "then validator should miss in the configuration" {
                config.validation.before.shouldBeNull()
            }
        }

        "when some validator is registered in the configuration builder" - {
            val config: JsArrayReaderConfig = arrayReaderConfig {
                this.validation {
                    this.before = ArrayValidator.isNotEmpty
                }
            }

            "then validator should present in the configuration" {
                config.validation.before.shouldNotBeNull()
            }
        }
    }
}
