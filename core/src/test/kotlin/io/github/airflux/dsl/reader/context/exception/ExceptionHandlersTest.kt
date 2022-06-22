package io.github.airflux.dsl.reader.context.exception

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ExceptionHandlersTest : FreeSpec() {

    companion object {
        private val handlerOfSpecificException: ExceptionHandler = { _: JsReaderContext, _: JsLocation, _: Throwable ->
            JsonErrors.PathMissing
        }

        private val handlerOfGenericException: ExceptionHandler = { _: JsReaderContext, _: JsLocation, _: Throwable ->
            JsonErrors.PathMissing
        }
    }

    init {

        "ExceptionHandlers#get" - {
            val exception = IllegalArgumentException()

            "when the most specific exception is first" - {
                val handlers = ExceptionHandlers(
                    listOf(
                        IllegalArgumentException::class to handlerOfSpecificException,
                        Exception::class to handlerOfGenericException
                    )
                )

                "should return the handler for the most specific an exception" {
                    val handler = handlers[exception]
                    handler shouldBe handlerOfSpecificException
                }
            }

            "when the most specific exception is not first" - {
                val handlers = ExceptionHandlers(
                    listOf(
                        Exception::class to handlerOfGenericException,
                        IllegalArgumentException::class to handlerOfSpecificException
                    )
                )

                "should return the handler for the most generic an exception" {
                    val handler = handlers[exception]
                    handler shouldBe handlerOfGenericException
                }
            }

            "when no the exception handlers" - {
                val handlers = ExceptionHandlers(emptyList())

                "should return the null value" {
                    val handler = handlers[exception]
                    handler.shouldBeNull()
                }
            }
        }
    }
}
