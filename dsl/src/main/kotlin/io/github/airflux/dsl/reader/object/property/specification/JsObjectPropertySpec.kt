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

public sealed interface JsObjectPropertySpec<T : Any> {
    public val path: JsPaths

    public sealed interface Required<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T>

        public infix fun validation(validator: JsValidator<T>): Required<T>
        public infix fun or(alt: Required<T>): Required<T>
    }

    public sealed interface Defaultable<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T>

        public infix fun validation(validator: JsValidator<T>): Defaultable<T>
        public infix fun or(alt: Defaultable<T>): Defaultable<T>
    }

    public sealed interface Optional<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T?>

        public infix fun validation(validator: JsValidator<T?>): Optional<T>
        public infix fun filter(predicate: JsPredicate<T>): Optional<T>
        public infix fun or(alt: Optional<T>): Optional<T>
    }

    public sealed interface OptionalWithDefault<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T>

        public infix fun validation(validator: JsValidator<T>): OptionalWithDefault<T>
        public infix fun or(alt: OptionalWithDefault<T>): OptionalWithDefault<T>
    }

    public sealed interface Nullable<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T?>

        public infix fun validation(validator: JsValidator<T?>): Nullable<T>
        public infix fun filter(predicate: JsPredicate<T>): Nullable<T>
        public infix fun or(alt: Nullable<T>): Nullable<T>
    }

    public sealed interface NullableWithDefault<T : Any> : JsObjectPropertySpec<T> {
        public val reader: JsReader<T?>

        public infix fun validation(validator: JsValidator<T?>): NullableWithDefault<T>
        public infix fun filter(predicate: JsPredicate<T>): NullableWithDefault<T>
        public infix fun or(alt: NullableWithDefault<T>): NullableWithDefault<T>
    }
}
