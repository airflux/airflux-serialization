package io.github.airflux.dsl.reader.context

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.dsl.reader.context.exception.ExceptionsHandler
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsReaderContextBuilderTest : FreeSpec() {

    init {

        "when any parameters are not set in the builder" - {
            val context: JsReaderContext = readerContext({})

            "then the context is empty" {
                context.isEmpty shouldBe true
            }
        }

        "when failFast parameter is set in the builder" - {
            val context: JsReaderContext = readerContext {
                failFast = false
            }

            "then the context should contain the FailFast option" {
                context.contains(FailFast) shouldBe true
            }
        }

        "when the error builder was registered" - {
            val context: JsReaderContext = readerContext {
                errorBuilders {
                    +PathMissingErrorBuilder { JsonErrors.PathMissing }
                }
            }

            "then the context should contain registered the error builder" {
                context.contains(PathMissingErrorBuilder) shouldBe true
            }
        }

        "when the exception handler was registered" - {
            val context: JsReaderContext = readerContext {
                exceptions {
                    exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
                }
            }

            "then the context should contain registered the exception handler" {
                context.contains(ExceptionsHandler) shouldBe true
            }
        }
    }
}
