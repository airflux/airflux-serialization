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

package io.github.airflux

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
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

                "the value as the JsStruct type" - {

                    //TODO
                    "with the value as the JsNumber type" {
                        val json = """{"id": 123}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsStruct>()
                        val id = root["id"]
                        val value = id.shouldBeInstanceOf<JsNumber>()
                        value.get shouldBe "123"
                    }

                    "the value as the JsString type" {
                        val json = """{"name": "user-1"}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsStruct>()
                        val name = root["name"]
                        val value = name.shouldBeInstanceOf<JsString>()
                        value.get shouldBe "user-1"
                    }

                    "the value as the JsBoolean type" - {

                        "true" {
                            val json = """{"isActive": true}""".deserialization()

                            val root = json.shouldBeInstanceOf<JsStruct>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<JsBoolean.True>()
                        }

                        "false" {
                            val json = """{"isActive": false}""".deserialization()

                            val root = json.shouldBeInstanceOf<JsStruct>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<JsBoolean.False>()
                        }
                    }

                    "the value as the JsNull type" {
                        val json = """{"title": null}""".deserialization()

                        val root = json.shouldBeInstanceOf<JsStruct>()
                        val title = root["title"]
                        title.shouldBeInstanceOf<JsNull>()
                    }
                }

                "the value as the JsArray type" {
                    val json = """["123"]""".deserialization()

                    val root = json.shouldBeInstanceOf<JsArray>()

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

                    "with property as the null value" {
                        val json = JsStruct("id" to JsNull)
                        val value = json.serialization()
                        value shouldBe """{"id":null}"""
                    }

                    "with property as a string value" {
                        val json = JsStruct("name" to JsString("user-1"))
                        val value = json.serialization()
                        value shouldBe """{"name":"user-1"}"""
                    }

                    "with property as a number value" {
                        val json = JsStruct("id" to JsNumber.valueOf("123")!!)
                        val value = json.serialization()
                        value shouldBe """{"id":123}"""
                    }

                    "with property as a boolean value" - {

                        "true" {
                            val json = JsStruct("isActive" to JsBoolean.True)
                            val value = json.serialization()
                            value shouldBe """{"isActive":true}"""
                        }

                        "false" {
                            val json = JsStruct("isActive" to JsBoolean.False)
                            val value = json.serialization()
                            value shouldBe """{"isActive":false}"""
                        }
                    }
                }
            }
        }
    }
}
