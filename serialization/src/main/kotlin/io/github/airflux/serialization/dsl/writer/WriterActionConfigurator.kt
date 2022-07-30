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

package io.github.airflux.serialization.dsl.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.writer.context.WriterContext

public typealias WriterActionBuilderIfResultIsEmpty = (WriterContext, JsLocation) -> WriterActionIfResultIsEmpty

internal interface WriterActionConfigurator {
    var actionIfEmpty: WriterActionBuilderIfResultIsEmpty

    fun returnEmptyValue(): WriterActionBuilderIfResultIsEmpty
    fun returnNothing(): WriterActionBuilderIfResultIsEmpty
    fun returnNullValue(): WriterActionBuilderIfResultIsEmpty
}

internal class WriterActionConfiguratorInstance(
    override var actionIfEmpty: WriterActionBuilderIfResultIsEmpty
) : WriterActionConfigurator {

    override fun returnEmptyValue(): WriterActionBuilderIfResultIsEmpty = returnEmptyValue
    override fun returnNothing(): WriterActionBuilderIfResultIsEmpty = returnNothing
    override fun returnNullValue(): WriterActionBuilderIfResultIsEmpty = returnNullValue

    companion object {
        private val returnEmptyValue: WriterActionBuilderIfResultIsEmpty =
            { _, _ -> WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE }
        private val returnNothing: WriterActionBuilderIfResultIsEmpty =
            { _, _ -> WriterActionIfResultIsEmpty.RETURN_NOTHING }
        private val returnNullValue: WriterActionBuilderIfResultIsEmpty =
            { _, _ -> WriterActionIfResultIsEmpty.RETURN_NULL_VALUE }
    }
}
