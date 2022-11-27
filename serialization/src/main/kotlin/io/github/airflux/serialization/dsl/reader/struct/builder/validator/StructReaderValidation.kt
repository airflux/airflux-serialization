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

package io.github.airflux.serialization.dsl.reader.struct.builder.validator

import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties

public interface StructReaderValidation<EB, CTX> {

    public fun validation(block: Builder<EB, CTX>.() -> Unit)

    @AirfluxMarker
    public class Builder<EB, CTX> internal constructor() {

        private val builders = linkedMapOf<StructValidatorBuilder.Key<*>, StructValidatorBuilder<EB, CTX>>()

        public operator fun <T : StructValidatorBuilder<EB, CTX>> T.unaryPlus(): Unit = add(this)

        public operator fun <T : StructValidatorBuilder<EB, CTX>> Collection<T>.unaryPlus(): Unit = addAll(this)

        public operator fun <T : StructValidatorBuilder<EB, CTX>> T.unaryMinus(): Unit = remove(this)

        public operator fun <T : StructValidatorBuilder<EB, CTX>> Collection<T>.unaryMinus(): Unit = removeAll(this)

        public fun <T : StructValidatorBuilder<EB, CTX>> add(item: T) {
            remove(item)
            builders[item.key] = item
        }

        public fun <T : StructValidatorBuilder<EB, CTX>> addAll(items: Collection<T>) {
            items.forEach { item -> add(item) }
        }

        public fun <T : StructValidatorBuilder<EB, CTX>> remove(item: T) {
            builders.remove(item.key)
        }

        public fun <T : StructValidatorBuilder<EB, CTX>> removeAll(items: Collection<T>) {
            items.forEach { item -> remove(item) }
        }

        internal fun build(): List<StructValidatorBuilder<EB, CTX>> = builders.values.toList()
    }
}

internal class StructReaderValidationInstance<EB, CTX> : StructReaderValidation<EB, CTX> {
    private val validationBuilder: StructReaderValidation.Builder<EB, CTX> = StructReaderValidation.Builder()

    override fun validation(block: StructReaderValidation.Builder<EB, CTX>.() -> Unit) {
        validationBuilder.block()
    }

    fun build(properties: StructProperties<EB, CTX>): List<StructValidator<EB, CTX>> =
        validationBuilder.build()
            .map { builder -> builder.build(properties) }
            .takeIf { it.isNotEmpty() }
            .orEmpty()
}
