/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.validator.JsPropertyValidator

sealed interface JsReaderProperty {

    sealed interface Required<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T>): Required<T>
    }

    sealed interface Defaultable<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T>): Defaultable<T>
    }

    sealed interface Optional<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T?>): Optional<T>
        infix fun filter(predicate: JsPredicate<T>): Optional<T>
    }

    sealed interface OptionalWithDefault<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T>): OptionalWithDefault<T>
    }

    sealed interface Nullable<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T?>): Nullable<T>
        infix fun filter(predicate: JsPredicate<T>): Nullable<T>
    }

    sealed interface NullableWithDefault<T : Any> : JsReaderProperty {
        infix fun validation(validator: JsPropertyValidator<T?>): NullableWithDefault<T>
        infix fun filter(predicate: JsPredicate<T>): NullableWithDefault<T>
    }
}
