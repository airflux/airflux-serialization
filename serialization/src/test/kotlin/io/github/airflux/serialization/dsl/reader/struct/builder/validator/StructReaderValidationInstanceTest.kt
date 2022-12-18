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

package io.github.airflux.serialization.dsl.reader.struct.builder.validator

import io.github.airflux.serialization.common.DummyStructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should

internal class StructReaderValidationInstanceTest : FreeSpec() {

    companion object {
        private val PROPERTIES = StructProperties<Unit, Unit>(emptyList())
    }

    init {

        "The StructReaderValidationInstance type" - {

            "when validators were not added" - {
                val validatorsBuilder = StructReaderValidationInstance<Unit, Unit>()

                "then the builder returns an empty collection" {
                    val validators = validatorsBuilder.build(PROPERTIES)
                    validators should beEmpty()
                }
            }

            "when some validators were added" - {
                val firstValidatorBuilder = DummyStructValidatorBuilder<Unit, Unit>(
                    key = DummyStructValidatorBuilder.key<Unit, Unit, DummyStructValidatorBuilder<Unit, Unit>>(),
                    result = null
                )
                val secondValidatorBuilder = DummyStructValidatorBuilder<Unit, Unit>(
                    key = DummyStructValidatorBuilder.key<Unit, Unit, DummyStructValidatorBuilder<Unit, Unit>>(),
                    result = null
                )

                val validatorsBuilder = StructReaderValidationInstance<Unit, Unit>().apply {
                    validation(firstValidatorBuilder, secondValidatorBuilder)
                }

                "then the builder returns a collection of validators" {
                    val validators = validatorsBuilder.build(PROPERTIES)
                    validators shouldContainExactly listOf(
                        firstValidatorBuilder.validator,
                        secondValidatorBuilder.validator
                    )
                }
            }
        }
    }
}
