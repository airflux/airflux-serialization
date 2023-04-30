/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.JsWriter

/**
 * Writer for primitive [Byte] type.
 */
public fun <O, CTX> byteWriter(): JsWriter<O, CTX, Byte> =
    JsWriter { _, _, _, value -> JsNumeric.valueOf(value) }
