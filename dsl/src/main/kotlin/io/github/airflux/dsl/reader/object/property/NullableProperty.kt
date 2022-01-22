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

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsPropertyValidator
import io.github.airflux.core.value.JsValue

sealed interface NullableProperty<T : Any> : JsReaderProperty {

    fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T?>

    fun validation(validator: JsPropertyValidator<T?>): NullableProperty<T>

    fun filter(predicate: JsPredicate<T>): NullableProperty<T>
}
