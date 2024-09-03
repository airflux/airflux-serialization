/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.env

import io.github.airflux.serialization.core.context.JsContext

/**
 * The environment of reading.
 * @param config the config of reading.
 * @param context the context of reading.
 */
public class JsReaderEnv<EB, O>(
    public val config: Config<EB, O>,
    public val context: JsContext = JsContext.Empty
) {

    /**
     * The configuration of reading.
     * @param errorBuilders the builders of error.
     * @param options the options of reading.
     */
    public class Config<EB, O>(
        public val errorBuilders: EB,
        public val options: O
    )
}

public operator fun <EB, O> JsReaderEnv<EB, O>.plus(element: JsContext.Element): JsReaderEnv<EB, O> =
    JsReaderEnv(config, this.context + element)
