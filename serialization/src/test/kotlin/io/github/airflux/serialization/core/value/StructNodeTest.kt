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
import io.github.airflux.serialization.core.path.PropertyPath
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class StructNodeTest {

    companion object {
        private const val USER_NAME_VALUE = "user"
        private val USER_NAME = StringNode(USER_NAME_VALUE)

        private const val IS_ACTIVE_VALUE = true
        private val IS_ACTIVE = BooleanNode.valueOf(IS_ACTIVE_VALUE)

        private val EMPTY_STRUCT = StructNode()
        private val NOT_EMPTY_STRUCT = StructNode(
            "name" to USER_NAME,
            "isActive" to IS_ACTIVE
        )
    }

    @Test
    fun isEmpty() {
        assertTrue(EMPTY_STRUCT.isEmpty())
    }

    @Test
    fun isNotEmpty() {
        assertFalse(NOT_EMPTY_STRUCT.isEmpty())
    }

    @Test
    fun sizeEmptyStruct() {
        assertEquals(0, EMPTY_STRUCT.count)
    }

    @Test
    fun sizeNotEmptyStruct() {
        assertEquals(2, NOT_EMPTY_STRUCT.count)
    }

    @Test
    fun getByNameFromEmptyStruct() {

        val value = EMPTY_STRUCT["name"]

        assertNull(value)
    }

    @Test
    fun getByNameFromNotEmptyStruct() {

        val value = NOT_EMPTY_STRUCT["name"]

        assertNotNull(value)
        value as StringNode
        assertEquals(USER_NAME_VALUE, value.get)
    }

    @Test
    fun getByKeyFromEmptyStruct() {
        val key = PropertyPath.Element.Key("name")

        val value = EMPTY_STRUCT[key]

        assertNull(value)
    }

    @Test
    fun getByKeyFromNotEmptyStruct() {
        val key = PropertyPath.Element.Key("name")

        val value = NOT_EMPTY_STRUCT[key]

        assertNotNull(value)
        value as StringNode
        assertEquals(USER_NAME_VALUE, value.get)
    }

    @Test
    fun iterable() {

        val items = NOT_EMPTY_STRUCT.map { (key, value) -> key to value }

        assertContains(items, "name" to USER_NAME)
        assertContains(items, "isActive" to IS_ACTIVE)
    }

    @Test
    fun `Testing the toString function of the StructNode class`() {
        val userName = "user"
        val isActive = true

        ObjectContract.checkToString(
            StructNode(
                "name" to StringNode(userName),
                "isActive" to BooleanNode.valueOf(isActive)
            ),
            """{"name": "$userName", "isActive": $isActive}"""
        )
    }

    @Test
    fun `Testing the equals contract of the StructNode class`() {
        val firstUserName = "user-1"
        val secondUserName = "user-2"
        val isActive = true

        ObjectContract.checkEqualsContract(
            StructNode("name" to StringNode(firstUserName)),
            StructNode("name" to StringNode(secondUserName)),
            StructNode("isActive" to BooleanNode.valueOf(isActive)),
        )
    }
}
