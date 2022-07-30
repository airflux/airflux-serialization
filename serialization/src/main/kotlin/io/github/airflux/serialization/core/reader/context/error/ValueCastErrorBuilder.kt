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

package io.github.airflux.serialization.core.reader.context.error

import io.github.airflux.serialization.core.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.serialization.core.context.error.ContextErrorBuilderKey
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.reader.result.ReaderResult
import kotlin.reflect.KClass

/**
 * The builder of an error that occurs when an element cannot be converted to the specified type.
 */
public class ValueCastErrorBuilder(private val builder: (value: String, target: KClass<*>) -> ReaderResult.Error) :
    AbstractErrorBuilderContextElement<ValueCastErrorBuilder>(key = ValueCastErrorBuilder) {

    public fun build(value: String, target: KClass<*>): ReaderResult.Error = builder(value, target)

    public companion object Key : ContextErrorBuilderKey<ValueCastErrorBuilder> {
        override val name: String = errorBuilderName()
    }
}
