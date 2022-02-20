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

package io.github.airflux.dsl.reader.`object`.property.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

sealed interface JsReaderPropertySpec<T : Any> {
    val path: JsPaths

    sealed interface Required<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T>

        infix fun validation(validator: JsValidator<T>): Required<T>
        infix fun or(alt: Required<T>): Required<T>
    }

    sealed interface Defaultable<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T>

        infix fun validation(validator: JsValidator<T>): Defaultable<T>
        infix fun or(alt: Defaultable<T>): Defaultable<T>
    }

    sealed interface Optional<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T?>

        infix fun validation(validator: JsValidator<T?>): Optional<T>
        infix fun filter(predicate: JsPredicate<T>): Optional<T>
        infix fun or(alt: Optional<T>): Optional<T>
    }

    sealed interface OptionalWithDefault<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T>

        infix fun validation(validator: JsValidator<T>): OptionalWithDefault<T>
        infix fun or(alt: OptionalWithDefault<T>): OptionalWithDefault<T>
    }

    sealed interface Nullable<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T?>

        infix fun validation(validator: JsValidator<T?>): Nullable<T>
        infix fun filter(predicate: JsPredicate<T>): Nullable<T>
        infix fun or(alt: Nullable<T>): Nullable<T>
    }

    sealed interface NullableWithDefault<T : Any> : JsReaderPropertySpec<T> {
        val reader: JsReader<T?>

        infix fun validation(validator: JsValidator<T?>): NullableWithDefault<T>
        infix fun filter(predicate: JsPredicate<T>): NullableWithDefault<T>
        infix fun or(alt: NullableWithDefault<T>): NullableWithDefault<T>
    }
}
