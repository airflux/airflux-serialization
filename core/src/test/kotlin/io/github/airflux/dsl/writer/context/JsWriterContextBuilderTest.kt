/*
 * Copyright 2021-2022 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.writer.context

import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.ActionOfWriterIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.ActionOfWriterIfObjectIsEmpty
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
                actionOfWriterIfArrayIsEmpty = ActionOfWriterIfArrayIsEmpty.Action.NONE
            }

            "then the context should contain the ActionOfWriterIfArrayIsEmpty option" {
                context.contains(ActionOfWriterIfArrayIsEmpty) shouldBe true
            }
        }

        "when writeActionIfObjectIsEmpty parameter is set in the builder" - {
            val context: JsWriterContext = writerContext {
                actionOfWriterIfObjectIsEmpty = ActionOfWriterIfObjectIsEmpty.Action.NONE
            }

            "then the context should contain the ActionOfWriterIfObjectIsEmpty option" {
                context.contains(ActionOfWriterIfObjectIsEmpty) shouldBe true
            }
        }
    }
}
