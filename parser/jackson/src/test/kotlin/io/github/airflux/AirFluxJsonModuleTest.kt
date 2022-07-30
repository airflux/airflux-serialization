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
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class AirFluxJsonModuleTest : FreeSpec() {

    companion object {
        private val mapper = ObjectMapper().apply {
            registerModule(AirFluxJsonModule)
        }

        private fun String.deserialization(): ValueNode = mapper.readValue(this, ValueNode::class.java)
        private fun ValueNode.serialization(): String = mapper.writeValueAsString(this)
    }

    init {

        "Deserialization" - {

            "success" - {

                "the value as the StructNode type" - {

                    //TODO
                    "with the value as the NumberNode type" {
                        val json = """{"id": 123}""".deserialization()

                        val root = json.shouldBeInstanceOf<StructNode>()
                        val id = root["id"]
                        val value = id.shouldBeInstanceOf<NumberNode>()
                        value.get shouldBe "123"
                    }

                    "the value as the StringNode type" {
                        val json = """{"name": "user-1"}""".deserialization()

                        val root = json.shouldBeInstanceOf<StructNode>()
                        val name = root["name"]
                        val value = name.shouldBeInstanceOf<StringNode>()
                        value.get shouldBe "user-1"
                    }

                    "the value as the BooleanNode type" - {

                        "true" {
                            val json = """{"isActive": true}""".deserialization()

                            val root = json.shouldBeInstanceOf<StructNode>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<BooleanNode.True>()
                        }

                        "false" {
                            val json = """{"isActive": false}""".deserialization()

                            val root = json.shouldBeInstanceOf<StructNode>()
                            val isActive = root["isActive"]
                            isActive.shouldBeInstanceOf<BooleanNode.False>()
                        }
                    }

                    "the value as the NullNode type" {
                        val json = """{"title": null}""".deserialization()

                        val root = json.shouldBeInstanceOf<StructNode>()
                        val title = root["title"]
                        title.shouldBeInstanceOf<NullNode>()
                    }
                }

                "the value as the ArrayNode type" {
                    val json = """["123"]""".deserialization()

                    val root = json.shouldBeInstanceOf<ArrayNode<StringNode>>()

                    root.size shouldBe 1
                    root[0] shouldBe StringNode("123")
                }
            }
        }

        "Serialization" - {

            "success" - {

                "write the value as the array" - {
                    val json = ArrayNode(StringNode("123"))
                    val value = json.serialization()
                    value shouldBe """["123"]"""
                }

                "write the value as the object" - {

                    "with attribute as the null value" {
                        val json = StructNode("id" to NullNode)
                        val value = json.serialization()
                        value shouldBe """{"id":null}"""
                    }

                    "with attribute as a string value" {
                        val json = StructNode("name" to StringNode("user-1"))
                        val value = json.serialization()
                        value shouldBe """{"name":"user-1"}"""
                    }

                    "with attribute as a number value" {
                        val json = StructNode("id" to NumberNode.valueOf(123))
                        val value = json.serialization()
                        value shouldBe """{"id":123}"""
                    }

                    "with attribute as a boolean value" - {

                        "true" {
                            val json = StructNode("isActive" to BooleanNode.True)
                            val value = json.serialization()
                            value shouldBe """{"isActive":true}"""
                        }

                        "false" {
                            val json = StructNode("isActive" to BooleanNode.False)
                            val value = json.serialization()
                            value shouldBe """{"isActive":false}"""
                        }
                    }
                }
            }
        }
    }
}
