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

package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

typealias BeforeValidatorInitializer =
        (config: ObjectReaderConfiguration, properties: List<JsReaderProperty>) -> JsObjectValidator.Before?

typealias AfterValidatorInitializer =
        (config: ObjectReaderConfiguration, properties: List<JsReaderProperty>) -> JsObjectValidator.After?

class JsObjectValidators private constructor(
    val before: JsObjectValidator.Before?,
    val after: JsObjectValidator.After?
) {

    class Builder {
        var before: BeforeValidatorInitializer? = null
        var after: AfterValidatorInitializer? = null

        fun before(block: BeforeValidatorInitializer?) {
            before = block
        }

        fun after(block: AfterValidatorInitializer?) {
            after = block
        }

        fun build(config: ObjectReaderConfiguration, properties: List<JsReaderProperty>): JsObjectValidators =
            JsObjectValidators(
                before = before?.invoke(config, properties),
                after = after?.invoke(config, properties)
            )
    }
}
