/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.writer.env

import io.github.airflux.serialization.core.context.JsAbstractContextElement
import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsWriterEnvTest : FreeSpec() {

    init {

        "The `JsWriterEnv` type" - {

            "when the environment instance is created with empty context" - {
                val env = JsWriterEnv(config = JsWriterEnv.Config(options = options))

                "then the environment instance should contain the empty context" {
                    env.context.isEmpty shouldBe true
                }
            }

            "when the environment instance is created with a some element in the context" - {
                val env = JsWriterEnv(config = JsWriterEnv.Config(options = options), context = UserContextElement())

                "then the environment instance should contain context with the passed elements" {
                    env.context.isEmpty shouldBe false
                    env.context.contains(UserContextElement) shouldBe true
                }

                "when the new context element is added to the environment" - {
                    val newEnv = env + OrderContextElement()

                    "then the environment instance should contain the context with the new elements" {
                        newEnv.context.isEmpty shouldBe false
                        newEnv.context.contains(UserContextElement) shouldBe true
                        newEnv.context.contains(OrderContextElement) shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private val options = OPTS(failFast = true)
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption

    private class UserContextElement : JsAbstractContextElement<UserContextElement>(UserContextElement) {
        companion object Key : JsContext.Key<UserContextElement>
    }

    private class OrderContextElement : JsAbstractContextElement<OrderContextElement>(OrderContextElement) {
        companion object Key : JsContext.Key<OrderContextElement>
    }
}
