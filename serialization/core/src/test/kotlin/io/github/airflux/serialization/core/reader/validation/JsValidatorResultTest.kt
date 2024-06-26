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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsValidatorResultTest : FreeSpec() {

    init {
        "The JsValidatorResult type" - {

            "the extension function `isValid`" - {

                "when value is valid" - {
                    val value = valid()

                    "then should return the true" {
                        value.isValid() shouldBe true
                    }
                }

                "when value is invalid" - {
                    val value = invalid(location = JsLocation, error = JsonErrors.PathMissing)

                    "then should return the false" {
                        value.isValid() shouldBe false
                    }
                }
            }

            "the extension function `isInvalid`" - {

                "when value is valid" - {
                    val value = valid()

                    "then should return the false" {
                        value.isInvalid() shouldBe false
                    }
                }

                "when value is invalid" - {
                    val value = invalid(location = JsLocation, error = JsonErrors.PathMissing)

                    "then should return the true" {
                        value.isInvalid() shouldBe true
                    }
                }
            }

            "the extension function `getOrNull`" - {

                "when value is valid" - {
                    val value = valid()

                    "then should return the null value" {
                        value.getOrNull() shouldBe null
                    }
                }

                "when value is invalid" - {
                    val value = invalid(location = JsLocation, error = JsonErrors.PathMissing)

                    "then should return an error" {
                        value.getOrNull() shouldBe JsReaderResult.Failure(
                            location = JsLocation,
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }

            "the extension function `fold`" - {

                "when value is valid" - {
                    val value = valid()

                    "then should be executed the `ifValid` code block" {
                        var result = 0
                        value.fold(
                            onValid = { result = 10 },
                            onInvalid = { result = 20 }
                        )
                        result shouldBe 10
                    }
                }

                "when value is invalid" - {
                    val value = invalid(location = JsLocation, error = JsonErrors.PathMissing)

                    "then should be executed the `ifInvalid` code block" {
                        var result = 0
                        value.fold(
                            onValid = { result = 10 },
                            onInvalid = { result = 20 }
                        )
                        result shouldBe 20
                    }
                }
            }

            "the extension function `ifInvalid`" - {

                "when value is valid" - {
                    val value = valid()

                    "then the `ifInvalid` method should not do any action" {
                        var result = false
                        value.ifInvalid { result = true }
                        result shouldBe false
                    }
                }

                "when value is invalid" - {
                    val value = invalid(location = JsLocation, error = JsonErrors.PathMissing)

                    "then the ifInvalid method should do some action" {
                        var result = false
                        value.ifInvalid { result = true }
                        result shouldBe true
                    }
                }
            }
        }
    }
}
