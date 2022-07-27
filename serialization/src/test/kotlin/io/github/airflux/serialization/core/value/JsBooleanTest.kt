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
import kotlin.test.Test
import kotlin.test.assertEquals

internal class JsBooleanTest {

    @Test
    fun `Testing the toString function of the JsBoolean class`() {
        ObjectContract.checkToString(JsBoolean.valueOf(true), "true")
        ObjectContract.checkToString(JsBoolean.valueOf(false), "false")
    }

    @Test
    fun `Testing inner state of the JsBoolean class`() {
        assertEquals(true, JsBoolean.valueOf(true).get)
        assertEquals(false, JsBoolean.valueOf(false).get)
    }
}
