package io.github.airflux.dsl.writer.context

import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.WriteActionIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsWriterContextBuilderTest : FreeSpec() {

    init {

        "when any parameters are not set in the builder" - {
            val context: JsWriterContext = writerContext { }

            "then context is empty" {
                context.isEmpty shouldBe true
            }
        }

        "when writeActionIfArrayIsEmpty parameter is set in the builder" - {
            val context: JsWriterContext = writerContext {
                writeActionIfArrayIsEmpty = WriteActionIfArrayIsEmpty.Action.EMPTY
            }

            "then the context should contain the WriteActionIfArrayIsEmpty option" {
                context.contains(WriteActionIfArrayIsEmpty) shouldBe true
            }
        }

        "when writeActionIfObjectIsEmpty parameter is set in the builder" - {
            val context: JsWriterContext = writerContext {
                writeActionIfObjectIsEmpty = WriteActionIfObjectIsEmpty.Action.EMPTY
            }

            "then the context should contain the WriteActionIfObjectIsEmpty option" {
                context.contains(WriteActionIfObjectIsEmpty) shouldBe true
            }
        }
    }
}
