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

package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

sealed interface JsReaderProperty {
    val path: JsPaths

    sealed interface Required<T : Any> : JsReaderProperty
    sealed interface Defaultable<T : Any> : JsReaderProperty
    sealed interface Optional<T : Any> : JsReaderProperty
    sealed interface OptionalWithDefault<T : Any> : JsReaderProperty
    sealed interface Nullable<T : Any> : JsReaderProperty
    sealed interface NullableWithDefault<T : Any> : JsReaderProperty
}
