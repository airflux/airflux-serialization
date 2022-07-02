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

package io.github.airflux.core.value

import io.github.airflux.common.ObjectContract
import io.github.airflux.core.path.PathElement
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class JsObjectTest {

    companion object {
        private const val USER_NAME_VALUE = "user"
        private val USER_NAME = JsString(USER_NAME_VALUE)

        private const val IS_ACTIVE_VALUE = true
        private val IS_ACTIVE = JsBoolean.valueOf(IS_ACTIVE_VALUE)

        private val EMPTY_OBJECT = JsObject()
        private val NOT_EMPTY_OBJECT = JsObject(
            "name" to USER_NAME,
            "isActive" to IS_ACTIVE
        )
    }

    @Test
    fun isEmpty() {
        assertTrue(EMPTY_OBJECT.isEmpty())
    }

    @Test
    fun isNotEmpty() {
        assertFalse(NOT_EMPTY_OBJECT.isEmpty())
    }

    @Test
    fun sizeEmptyObject() {
        assertEquals(0, EMPTY_OBJECT.count)
    }

    @Test
    fun sizeNotEmptyObject() {
        assertEquals(2, NOT_EMPTY_OBJECT.count)
    }

    @Test
    fun getByNameFromEmptyObject() {

        val value = EMPTY_OBJECT["name"]

        assertNull(value)
    }

    @Test
    fun getByNameFromNotEmptyObject() {

        val value = NOT_EMPTY_OBJECT["name"]

        assertNotNull(value)
        value as JsString
        assertEquals(USER_NAME_VALUE, value.get)
    }

    @Test
    fun getByKeyFromEmptyObject() {
        val key = PathElement.Key("name")

        val value = EMPTY_OBJECT[key]

        assertNull(value)
    }

    @Test
    fun getByKeyFromNotEmptyObject() {
        val key = PathElement.Key("name")

        val value = NOT_EMPTY_OBJECT[key]

        assertNotNull(value)
        value as JsString
        assertEquals(USER_NAME_VALUE, value.get)
    }

    @Test
    fun iterable() {

        val items = NOT_EMPTY_OBJECT.map { (key, value) -> key to value }

        assertContains(items, "name" to USER_NAME)
        assertContains(items, "isActive" to IS_ACTIVE)
    }

    @Test
    fun `Testing the toString function of the JsObject class`() {
        val userName = "user"
        val isActive = true

        ObjectContract.checkToString(
            JsObject(
                "name" to JsString(userName),
                "isActive" to JsBoolean.valueOf(isActive)
            ),
            """{"name": "$userName", "isActive": $isActive}"""
        )
    }

    @Test
    fun `Testing the equals contract of the JsObject class`() {
        val firstUserName = "user-1"
        val secondUserName = "user-2"
        val isActive = true

        ObjectContract.checkEqualsContract(
            JsObject("name" to JsString(firstUserName)),
            JsObject("name" to JsString(secondUserName)),
            JsObject("isActive" to JsBoolean.valueOf(isActive)),
        )
    }
}
