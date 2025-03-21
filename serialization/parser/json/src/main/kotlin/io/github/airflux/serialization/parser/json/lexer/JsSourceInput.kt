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

public interface JsSourceInput {
    /**
     * The current position in the input.
     * The first character is at position 0.
     */
    public val position: Int

    /**
     * The current line in the input.
     * The first line is at position 1.
     */
    public val line: Int

    /**
     * The current column in the input.
     * The first column is at position 1.
     */
    public val column: Int

    /**
     * Indicates whether the end of the input has been reached.
     */
    public val isEOF: Boolean

    /**
     * Reads the next character from the input.
     * Returns `null` if the end of the input has been reached.
     */
    public fun nextChar(): Char?
}
