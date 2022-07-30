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
import io.github.airflux.serialization.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.serialization.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.serialization.core.path.PathElement
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ArrayNodeTest {

    companion object {
        private val EMPTY_ARRAY = ArrayNode<StringNode>()
        private val FIRST_ITEM = StringNode(FIRST_PHONE_VALUE)
        private val SECOND_ITEM = StringNode(SECOND_PHONE_VALUE)
        private val NOT_EMPTY_ARRAY = ArrayNode(FIRST_ITEM, SECOND_ITEM)
    }

    @Test
    fun isEmpty() {
        assertTrue(EMPTY_ARRAY.isEmpty())
    }

    @Test
    fun isNotEmpty() {
        assertFalse(NOT_EMPTY_ARRAY.isEmpty())
    }

    @Test
    fun sizeEmptyArray() {
        assertEquals(0, EMPTY_ARRAY.size)
    }

    @Test
    fun sizeNotEmptyArray() {
        assertEquals(2, NOT_EMPTY_ARRAY.size)
    }

    @Test
    fun getByIntFromEmptyArray() {
        assertNull(EMPTY_ARRAY[0])
    }

    @Test
    fun getByIntFromNotEmptyArray() {
        val item = NOT_EMPTY_ARRAY[0]

        assertNotNull(item)
        item as StringNode
        assertEquals(FIRST_PHONE_VALUE, item.get)
    }

    @Test
    fun getByIdxFromEmptyArray() {
        assertNull(EMPTY_ARRAY[PathElement.Idx(0)])
    }

    @Test
    fun getByIdxFromNotEmptyArray() {
        val item = NOT_EMPTY_ARRAY[PathElement.Idx(0)]

        assertNotNull(item)
        item as StringNode
        assertEquals(FIRST_PHONE_VALUE, item.get)
    }

    @Test
    fun iterable() {
        assertContains(NOT_EMPTY_ARRAY, FIRST_ITEM)
        assertContains(NOT_EMPTY_ARRAY, SECOND_ITEM)
    }

    @Test
    fun `Testing the toString function of the ArrayNode class`() {
        ObjectContract.checkToString(
            ArrayNode(
                StringNode(FIRST_PHONE_VALUE),
                StringNode(SECOND_PHONE_VALUE),
            ),
            """["$FIRST_PHONE_VALUE", "$SECOND_PHONE_VALUE"]"""
        )
    }

    //TODO
    @Test
    fun `Testing the equals contract of the StringNode class`() {
        ObjectContract.checkEqualsContract(
            ArrayNode(StringNode(FIRST_PHONE_VALUE)),
            ArrayNode(StringNode(FIRST_PHONE_VALUE)),
            ArrayNode(StringNode(SECOND_PHONE_VALUE)),
        )
    }
}
