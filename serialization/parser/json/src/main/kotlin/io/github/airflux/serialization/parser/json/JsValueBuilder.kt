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

package io.github.airflux.serialization.parser.json

import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue

@Suppress("TooManyFunctions")
internal class JsValueBuilder {
    private val stack: Stack<Element> = Stack()

    var result: JsValue? = null
        private set

    fun startObject() {
        stack.push(Element.Struct())
    }

    fun addProperty(name: String) {
        val struct = stack.popStruct()
        stack.push(struct.addProperty(name))
    }

    fun endObject() {
        addValue(stack.popStruct().build())
    }

    fun startArray() {
        stack.push(Element.Array())
    }

    fun endArray() {
        addValue(stack.popArray().build())
    }

    fun stringValue(value: String) {
        addValue(JsString(value))
    }

    fun booleanValue(value: Boolean) {
        addValue(JsBoolean.valueOf(value))
    }

    fun numberValue(value: String) {
        addValue(checkNotNull(JsNumber.valueOf(value)) { "Something got wrong. Invalid number value: '$value'" })
    }

    fun nullValue() {
        addValue(JsNull)
    }

    private fun addValue(value: JsValue) {
        check(result == null) { "Something got wrong. The current value is not null." }
        if (stack.isEmpty())
            result = value
        else {
            val previous = stack.pop()
            val new = previous.addValue(value)
            stack.push(new)
        }
    }

    private fun Stack<Element>.popStruct(): Element.Struct = pop() as Element.Struct

    private fun Stack<Element>.popArray(): Element.Array = pop() as Element.Array

    private sealed class Element {

        abstract fun addValue(value: JsValue): Element

        class Array : Element() {
            private val items: MutableList<JsValue> = mutableListOf()

            override fun addValue(value: JsValue): Element = this.apply { items.add(value) }

            fun build(): JsValue = JsArray(items)
        }

        class Struct : Element() {
            private val properties: MutableList<Pair<String, JsValue>> = mutableListOf()

            fun addProperty(name: String): Element = Property(name)

            override fun addValue(value: JsValue): Element = error("Cannot add a value on a struct without a key.")

            fun build(): JsValue = JsStruct(properties)

            inner class Property(private val name: String) : Element() {
                override fun addValue(value: JsValue): Element =
                    this@Struct.apply { properties.add(name to value) }
            }
        }
    }
}
