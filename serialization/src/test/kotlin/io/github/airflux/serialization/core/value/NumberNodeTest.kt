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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.common.ObjectContract
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class NumberNodeTest {

    @Test
    fun `Testing the valueOf function of the NumberNode class for Byte type`() {
        val min: Byte = Byte.MIN_VALUE
        val max: Byte = Byte.MAX_VALUE

        assertEquals(min, NumberNode.valueOf(min).get.toByte())
        assertEquals(max, NumberNode.valueOf(max).get.toByte())
    }

    @Test
    fun `Testing the valueOf function of the NumberNode class for Short type`() {
        val min: Short = Short.MIN_VALUE
        val max: Short = Short.MAX_VALUE

        assertEquals(min, NumberNode.valueOf(min).get.toShort())
        assertEquals(max, NumberNode.valueOf(max).get.toShort())
    }

    @Test
    fun `Testing the valueOf function of the NumberNode class for Int type`() {
        val min: Int = Int.MIN_VALUE
        val max: Int = Int.MAX_VALUE

        assertEquals(min, NumberNode.valueOf(min).get.toInt())
        assertEquals(max, NumberNode.valueOf(max).get.toInt())
    }

    @Test
    fun `Testing the valueOf function of the NumberNode class for Long type`() {
        val min: Long = Long.MIN_VALUE
        val max: Long = Long.MAX_VALUE

        assertEquals(min, NumberNode.valueOf(min).get.toLong())
        assertEquals(max, NumberNode.valueOf(max).get.toLong())
    }

    @TestFactory
    fun `Testing the valueOf function of the NumberNode class for String type`(): List<DynamicTest> = listOf(
        Triple("false", "false", null),
        Triple(".0", ".0", null),
        Triple("+.0", "+.0", null),
        Triple("-.0", "-.0", null),
        Triple("1", "1", "1"),
        Triple("-1", "-1", "-1"),
        Triple("1.5", "1.5", "1.5"),
        Triple("-1.5", "-1.5", "-1.5"),
        Triple("1.50", "1.50", "1.50"),
        Triple("-1.50", "-1.50", "-1.50")
    ).map { (displayName: String, text: String, expected: String?) ->
        DynamicTest.dynamicTest(displayName) {
            assertEquals(expected, NumberNode.valueOf(text)?.get)
        }
    }

    @TestFactory
    fun `Testing the internal representation as an integer number`(): List<DynamicTest> = listOf(
        "0" to "0", "-0" to "-0",
        "1" to "1", "-1" to "-1",
        "100" to "100", "-100" to "-100"
    ).map { (displayName: String, text: String) ->
        DynamicTest.dynamicTest(displayName) {
            val number = NumberNode.valueOf(text)!!
            assertTrue(number.isInteger)
            assertFalse(number.isReal)
        }
    }

    @TestFactory
    fun `Testing the internal representation as a real number`(): List<DynamicTest> = listOf(
        "0.0" to "0.0", "-0.0" to "-0.0",
        "0.1" to "0.1", "-0.1" to "-0.1",
        "1.5" to "1.5", "-1.5" to "-1.5",
        "1.50" to "1.50", "-1.50" to "-1.50"
    ).map { (displayName: String, text: String) ->
        DynamicTest.dynamicTest(displayName) {
            val number = NumberNode.valueOf(text)!!
            assertFalse(number.isInteger)
            assertTrue(number.isReal)
        }
    }

    @TestFactory
    fun `Testing the toString function of the NumberNode class`(): List<DynamicTest> = listOf(
        "1" to "1", "-1" to "-1",
        "1.5" to "1.5", "-1.5" to "-1.5",
        "1.50" to "1.50", "-1.50" to "-1.50"
    ).map { (displayName: String, text: String) ->
        DynamicTest.dynamicTest(displayName) {

            ObjectContract.checkToString(NumberNode.valueOf(text)!!.toString(), text)
        }
    }

    @Test
    fun `Testing the equals contract of the NumberNode class`() {
        ObjectContract.checkEqualsContract(
            NumberNode.valueOf(10),
            NumberNode.valueOf(10),
            NumberNode.valueOf(100)
        )
    }
}
