package io.github.airflux.dsl.reader.context.exception

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ExceptionsHandlerBuilderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val EXCEPTION = IllegalArgumentException()
    }

    init {

        "when an exception handler is not registered in the builder" - {
            val exceptionsHandler: ExceptionsHandler = ExceptionsHandlerBuilder().build()

            "then the handleException should return the null value" {
                val error = exceptionsHandler.handleException(CONTEXT, LOCATION, EXCEPTION)
                error.shouldBeNull()
            }
        }

        "when an exception handler is registered in the builder" - {
            val exceptionsHandler = ExceptionsHandlerBuilder()
                .apply {
                    exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
                }.build()

            "then the handleException should return the error" {
                val error = exceptionsHandler.handleException(CONTEXT, LOCATION, EXCEPTION)
                error shouldBe JsonErrors.PathMissing
            }
        }
    }
}
