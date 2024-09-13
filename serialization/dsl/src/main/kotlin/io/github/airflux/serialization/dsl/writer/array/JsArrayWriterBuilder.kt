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

package io.github.airflux.serialization.dsl.writer.array

import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.array.JsArrayWriter
import io.github.airflux.serialization.core.writer.array.buildArrayWriter
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption

public fun <O, T> arrayWriter(items: JsWriter<O, T>): JsArrayWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption = buildArrayWriter(items)
