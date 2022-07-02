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

package io.github.airflux

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.github.airflux.parser.AirFluxJsonModule
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class AirFluxJsonModuleTest : FreeSpec() {

    companion object {
        private val mapper = ObjectMapper().apply {
            registerModule(AirFluxJsonModule)
        }

        private fun String.deserialization(): JsValue = mapper.readValue(this, JsValue::class.java)
        private fun JsValue.serialization(): String = mapper.writeValueAsString(this)
    }

    init {

        "Deserialization" - {

            "success" - {

                "the value as the JsObject" - {

                    "with the value as the JsNumber" {
                        val json = """{"id": 123}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsObject>()
                        val id = root["id"]
                        val value = id.shouldBeInstanceOf<JsNumber>()
                        value.get shouldBe "123"
                    }

                    "the value as the JsString" {
                        val json = """{"name": "user-1"}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsObject>()
                        val name = root["name"]
                        val value = name.shouldBeInstanceOf<JsString>()
                        value.get shouldBe "user-1"
                    }

                    "the value as the JsBoolean" - {

                        "true" {
                            val json = """{"isActive": true}""".deserialization()

                            val root = json.shouldBeInstanceOf<JsObject>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<JsBoolean.True>()
                        }

                        "false" {
                            val json = """{"isActive": false}""".deserialization()

                            val root = json.shouldBeInstanceOf<JsObject>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<JsBoolean.False>()
                        }
                    }

                    "the value as the JsNull" {
                        val json = """{"title": null}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsObject>()
                        val title = root["title"]
                        title.shouldBeInstanceOf<JsNull>()
                    }
                }

                "the value as the JsArray" {
                    val json = """["123"]""".deserialization()

                    val root = json.shouldBeInstanceOf<JsArray<JsString>>()

                    root.size shouldBe 1
                    root[0] shouldBe JsString("123")
                }
            }
        }

        "Serialization" - {

            "success" - {

                "write the value as the array" - {
                    val json = JsArray(JsString("123"))
                    val value = json.serialization()
                    value shouldBe """["123"]"""
                }

                "write the value as the object" - {

                    "with attribute as the null value" {
                        val json = JsObject("id" to JsNull)
                        val value = json.serialization()
                        value shouldBe """{"id":null}"""
                    }

                    "with attribute as a string value" {
                        val json = JsObject("name" to JsString("user-1"))
                        val value = json.serialization()
                        value shouldBe """{"name":"user-1"}"""
                    }

                    "with attribute as a number value" {
                        val json = JsObject("id" to JsNumber.valueOf(123))
                        val value = json.serialization()
                        value shouldBe """{"id":123}"""
                    }

                    "with attribute as a boolean value" - {

                        "true" {
                            val json = JsObject("isActive" to JsBoolean.True)
                            val value = json.serialization()
                            value shouldBe """{"isActive":true}"""
                        }

                        "false" {
                            val json = JsObject("isActive" to JsBoolean.False)
                            val value = json.serialization()
                            value shouldBe """{"isActive":false}"""
                        }
                    }
                }
            }
        }
    }
}
