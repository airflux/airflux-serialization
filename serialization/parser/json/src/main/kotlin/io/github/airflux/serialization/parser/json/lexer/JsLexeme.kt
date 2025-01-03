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

package io.github.airflux.serialization.parser.json.lexer

import io.github.airflux.serialization.parser.json.CharBuffer

internal class JsLexeme private constructor(private val buffer: MutableCharBuffer) : CharBuffer by buffer {

    constructor() : this(MutableCharBuffer())

    internal var position: Int = 0
        private set

    internal var line: Int = 1
        private set

    internal var column: Int = 1
        private set

    internal fun initialize(position: Int, line: Int, column: Int) {
        buffer.clear()
        this.position = position
        this.line = line
        this.column = column
    }

    internal operator fun plusAssign(str: String) {
        buffer.append(str)
    }

    internal operator fun plusAssign(char: Char) {
        buffer.append(char)
    }
}
