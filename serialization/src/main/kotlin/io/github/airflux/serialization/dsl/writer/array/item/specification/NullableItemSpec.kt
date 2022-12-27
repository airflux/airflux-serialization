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

package io.github.airflux.serialization.dsl.writer.array.item.specification

import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.filter
import io.github.airflux.serialization.core.writer.predicate.WriterPredicate

public fun <CTX, T> nullable(writer: Writer<CTX, T & Any>): ArrayItemSpec.Nullable<CTX, T> =
    ArrayItemSpec.Nullable(writer)

public infix fun <CTX, T> ArrayItemSpec.Nullable<CTX, T>.filter(
    predicate: WriterPredicate<CTX, T & Any>
): ArrayItemSpec.Nullable<CTX, T> =
    ArrayItemSpec.Nullable(writer = writer.filter(predicate))
