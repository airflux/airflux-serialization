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

package io.github.airflux.serialization.dsl.reader.array.item.specification

import io.github.airflux.serialization.core.reader.Reader

public sealed class ArrayItemSpec<EB, O, in CTX, out T> {

    public abstract val reader: Reader<EB, O, CTX, T>

    public class NonNullable<EB, O, in CTX, out T> internal constructor(override val reader: Reader<EB, O, CTX, T>) :
        ArrayItemSpec<EB, O, CTX, T>()

    public class Nullable<EB, O, in CTX, out T> internal constructor(override val reader: Reader<EB, O, CTX, T?>) :
        ArrayItemSpec<EB, O, CTX, T?>()
}
